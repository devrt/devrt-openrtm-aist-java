package jp.go.aist.rtm.RTC.SDOPackage;

import java.util.UUID;
import java.util.Vector;

import jp.go.aist.rtm.RTC.ConfigAdmin;
import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import jp.go.aist.rtm.RTC.util.POAUtil;
import jp.go.aist.rtm.RTC.util.Properties;

import org.omg.CORBA.Any;

import _SDOPackage.Configuration;
import _SDOPackage.ConfigurationHelper;
import _SDOPackage.ConfigurationPOA;
import _SDOPackage.ConfigurationSet;
import _SDOPackage.ConfigurationSetListHolder;
import _SDOPackage.DeviceProfile;
import _SDOPackage.InternalError;
import _SDOPackage.InvalidParameter;
import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;
import _SDOPackage.NotAvailable;
import _SDOPackage.Organization;
import _SDOPackage.OrganizationListHolder;
import _SDOPackage.Parameter;
import _SDOPackage.ParameterListHolder;
import _SDOPackage.ServiceProfile;
import _SDOPackage.ServiceProfileHolder;
import _SDOPackage.ServiceProfileListHolder;


/**
 * <p>SDO Configuration 実装クラス<br />
 *
 * Configuration interface は Resource Data Model で定義されたデータの
 * 追加、削除等の操作を行うためのインターフェースです。
 * DeviceProfile, ServiceProfile, ConfigurationProfile および Organization
 * の変更を行うためのオペレーションを備えています。SDO の仕様ではアクセス制御
 * およびセキュリティに関する詳細については規定していません。<br />
 * 
 * 複数の設定 (Configuration) を保持することにより、容易かつ素早くある設定
 * を反映させることができます。事前に定義された複数の設定を ConfigurationSets
 * および configuration profile として保持することができます。ひとつの
 * ConfigurationSet は特定の設定に関連付けられた全プロパティ値のリストを、
 * ユニークID、詳細とともに持っています。これにより、各設定項目の詳細を記述し
 * 区別することができます。Configuration interface のオペレーションはこれら
 * ConfiguratioinSets の管理を支援します。</p>
 *
 * <ol>
 * <li>ConfigurationSet: id, description, NVList から構成される1セットの設定</li>
 * <li>ConfigurationSetList: ConfigurationSet のリスト</li>
 * <li>Parameter: name, type, allowed_values から構成されるパラメータ定義。</li>
 * <li>ActiveConfigurationSet: 現在有効なコンフィギュレーションの1セット。</li>
 * </ol>
 *
 * <p>以下、SDO仕様に明記されていないもしくは解釈がわからないため独自解釈</p>
 *
 * <p>以下の関数は ParameterList に対して処理を行います。</p>
 * <ol>
 * <li>get_configuration_parameters()</li>
 * </ol>
 *
 * <p>以下の関数はアクティブなConfigurationSetに対する処理を行います</p>
 * <ol>
 * <li>get_configuration_parameter_values()</li>
 * <li>get_configuration_parameter_value()</li>
 * <li>set_configuration_parameter()</li>
 * </ol>
 *
 * <p>以下の関数はConfigurationSetListに対して処理を行います。</p>
 * <ol>
 * <li>get_configuration_sets()</li>
 * <li>get_configuration_set()</li>
 * <li>set_configuration_set_values()</li>
 * <li>get_active_configuration_set()</li>
 * <li>add_configuration_set()</li>
 * <li>remove_configuration_set()</li>
 * <li>activate_configuration_set()</li>
 * </ol>
 */
public class Configuration_impl extends ConfigurationPOA {

    /**
     * <p>コンストラクタです。</p>
     *
     * @param configsets　コンフィギュレーション情報
     */
    public Configuration_impl(ConfigAdmin configsets){
        this.m_configsets = configsets;
        this.m_objref = this._this();
    }

    /**
     * <p>オブジェクト・リファレンスを取得します。</p>
     *
     * @return オブジェクト・リファレンス
     */
    public Configuration _this() {
        
        if (this.m_objref == null) {
            try {
                this.m_objref = ConfigurationHelper.narrow(POAUtil.getRef(this));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        
        return this.m_objref;
    }
    
    /**
     * <p>コンフィギュレーションセットをプロパティにコピーします。</p>
     *
     * @param prop　コピー先プロパティ
     * @param conf　コピー元コンフィギュレーションセット
     */
    private void toProperties(Properties prop, final ConfigurationSet conf) {
        NVListHolder nvlist = new NVListHolder();
        nvlist.value = conf.configuration_data;
        NVUtil.copyToProperties(prop, nvlist);
    }

    /**
     * <p>プロパティをコンフィギュレーションセットにコピーします。</p>
     *
     * @param conf　コピー先コンフィギュレーションセット
     * @param prop　コピー元プロパティ
     */
    private void toConfigurationSet(ConfigurationSet conf, final Properties prop) {
        conf.description = new String(prop.getProperty("description"));
        conf.id = new String(prop.getName());
        NVListHolder nvlist = new NVListHolder();
        NVUtil.copyFromProperties(nvlist, prop);
        conf.configuration_data = nvlist.value;
    }
    /**
     *
     * <p>[CORBA interface] SDO の DeviceProfile をセットします。<br />
     *
     * このオペレーションは SDO の DeviceProfile をセットします。SDO が
     * DeviceProfile を保持している場合は新たな DeviceProfile を生成し、
     * DeviceProfile をすでに保持している場合は既存のものと置き換えます。</p>
     *
     * @param dProfile SDO に関連付けられる DeviceProfile。
     * @return オペレーションが成功したかどうかを返す。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InvalidParameter 引数 "dProfile" が null である。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean set_device_profile(final DeviceProfile dProfile)
            throws InvalidParameter, NotAvailable, InternalError {
        try {
            if(m_deviceProfile == null) m_deviceProfile = new DeviceProfile();
            synchronized (m_deviceProfile) {
                m_deviceProfile = dProfile;
            }
        } catch(Exception ex) {
            throw new InternalError("Unknown Error:set_device_profile()");
        }
        return true;
    }

    /**
     * 
     * <p>[CORBA interface] SDO の ServiceProfile を設定します。<br />
     *
     * このオペレーションはこの Configuration interface を所有する対象 SDO の
     * ServiceProfile を設定します。もし引数の ServiceProfile の id が空であれば
     * 新しい ID が生成されその ServiceProfile を格納します。もし id が空で
     * なければ、SDO は同じ id を持つ ServiceProfile を検索します。
     * 同じ id が存在しなければこの ServiceProfile を追加し、id が存在すれば
     * 上書きをします。</p>
     *
     * @param sProfile 追加する ServiceProfile
     * @return オペレーションが成功したかどうかを返す。
     *
     * @exception InvalidParameter 引数 "sProfile" が nullである。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean set_service_profile(final ServiceProfile sProfile)
            throws InvalidParameter, NotAvailable, InternalError {
        try{
            if( m_serviceProfiles==null ) {
                m_serviceProfiles = new ServiceProfileListHolder();
                m_serviceProfiles.value = new ServiceProfile[0];
            }
            if( sProfile.id==null || sProfile.id.equals("") ) {
                ServiceProfileHolder prof = new ServiceProfileHolder(sProfile);
                prof.value.id = UUID.randomUUID().toString();
                CORBA_SeqUtil.push_back(m_serviceProfiles, prof.value);
                return true;
            }
            for(int index=0; index<m_serviceProfiles.value.length; index++ ) {
                if(sProfile.id.equals(m_serviceProfiles.value[index].id)) {
                    CORBA_SeqUtil.erase(m_serviceProfiles, index);
                    break;
                }
            }
            CORBA_SeqUtil.push_back(m_serviceProfiles, sProfile);
            return true;
        } catch (Exception ex) {
            throw new InternalError("Configuration::set_service_profile"); 
        }
    }

    /**
     * <p>[CORBA interface] Organization を追加します。<br />
     *
     * このオペレーションは Organization object のリファレンスを追加します。</p>
     *
     * @param org 追加する Organization
     * @return オペレーションが成功したかどうかを返す。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InvalidParameter 引数 "organization" が null である。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean add_organization(Organization org) 
            throws InvalidParameter, NotAvailable, InternalError {
        try {
            if( m_organizations==null ){
                m_organizations = new OrganizationListHolder(); 
                m_organizations.value = new Organization[0];
            }
            CORBA_SeqUtil.push_back(m_organizations, org);
        } catch (Exception ex) {
            throw new InternalError("Configuration::set_service_profile");
        }
        return true;
    }

    /**
     * <p>[CORBA interface] ServiceProfile を削除します。<br />
     *
     * このオペレーションはこの Configuration interface を持つ SDO の
     * Service の ServiceProfile を削除します。
     * 削除する ServiceProfileは引数により指定されます。</p>
     *
     * @param id 削除する ServcieProfile の serviceID。
     * @return オペレーションが成功したかどうかを返す。
     *
     * @exception InvalidParameter 引数 "id" が null である。もしくは "id" に
     *                             関連付けられた ServiceProfile が存在しない。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean remove_service_profile(final String id) 
            throws InvalidParameter, NotAvailable, InternalError {
        try {
            for(int index=0; index<m_serviceProfiles.value.length; index++ ) {
                if(id.equals(m_serviceProfiles.value[index].id)) {
                    CORBA_SeqUtil.erase(m_serviceProfiles, index);
                    return true;
                }
            }
        } catch(Exception ex) {
            throw new InternalError("Configuration::remove_service_profile");
        }
        return true;
    }

    /**
     * <p>[CORBA interface] Organization の参照を削除します。<br />
     *
     * このオペレーションは Organization の参照を削除します。</p>
     *
     * @param organization_id 削除する Organization の一意な id。
     * @return オペレーションが成功したかどうかを返す。
     *
     * @exception InvalidParameter 引数 "id" が null である。もしくは "id" に
     *                             関連付けられた Organization が存在しない。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean remove_organization(String organization_id)
            throws InvalidParameter, NotAvailable, InternalError {
        try {
            synchronized (m_organizations) {
                for(int index=0; index<m_organizations.value.length; index++ ) {
                    if(organization_id.equals(m_organizations.value[index].get_organization_id())) {
                        CORBA_SeqUtil.erase(m_organizations, index);
                        return true;
                    }
                }
            }
        } catch(Exception ex) {
            throw new InternalError("Configuration::remove_service_profile");
        }
        return true;
    }

    /**
     * <p>[CORBA interface] 設定パラメータのリストを取得します。<br />
     *
     * このオペレーションは configuration parameter のリストを返します。
     * SDO が設定可能なパラメータを持たなければ空のリストを返します。</p>
     *
     * @return 設定を特徴付けるパラメータ定義のリスト。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     *
     */
    public Parameter[] get_configuration_parameters()
            throws NotAvailable, InternalError {
        try{
            if( m_parameters==null ) {
                m_parameters = new ParameterListHolder();
                m_parameters.value = new Parameter[0];
            }
            synchronized (m_parameters) {
                ParameterListHolder param = new ParameterListHolder(m_parameters.value);
                return param.value;
            }
        } catch (Exception ex) {
            throw new InternalError("Configuration::get_configuration_parameters()");
        }
    }

    /**
     * <p>[CORBA interface] Configuration parameter の値のリストを取得します。<br />
     *
     * このオペレーションは configuration パラメータおよび値を返します。
     * ※未実装</p>
     *
     * @return 全ての configuration パラメータと値のリスト。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public synchronized NameValue[] get_configuration_parameter_values()
            throws NotAvailable, InternalError {
        NVListHolder nvlist = new NVListHolder();
        nvlist.value = new NameValue[0];
        return nvlist.value;
    }

    /**
     * <p>[CORBA interface] Configuration parameter の値を取得します。<br />
     *
     * このオペレーションは引数 "name" で指定されたパラメータ値を返します。
     * ※未実装</p>
     *
     * @param name 値を要求するパラメータの名前。
     * @return 指定されたパラメータの値。
     *
     * @exception InvalidParameter 引数 "name" が null である。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public Any get_configuration_parameter_value(String name)
            throws InvalidParameter, NotAvailable, InternalError {
        if( name==null || name.equals("") ) throw new InvalidParameter("Name is empty.");
        
        Any value = ORBUtil.getOrb().create_any();
        return value;
    }

    /**
     * <p>[CORBA interface] Configuration パラメータを変更します。<br />
     *
     * このオペレーションは "name" で指定したパラメータの値を "value" に変更します。
     * ※未実装</p>
     *
     * @param name 変更したいパラメータの名前。
     * @param value 変更したいパラメータの値。
     * @return オペレーションが成功したかどうかを返す。
     *
     * @exception InvalidParameter 引数 "name" が null である。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     *
     */
    public boolean set_configuration_parameter(String name, Any value)
            throws InvalidParameter, NotAvailable, InternalError {
        return true;
    }

    /**
     * <p>[CORBA interface] ConfigurationSet リストを取得します。<br /> 
     *
     * このオペレーションは ConfigurationProfile が持つ ConfigurationSet の
     * リストを返します。 SDO が ConfigurationSet を持たなければ空のリストを返します。</p>
     *
     * @return 保持している ConfigurationSet のリストの現在値。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public ConfigurationSet[] get_configuration_sets() 
                throws NotAvailable, InternalError {
        try {
            synchronized (m_configsets) {
                ConfigurationSetListHolder config_sets = new ConfigurationSetListHolder();
                config_sets.value = new ConfigurationSet[0];
                
                Vector<Properties> cf = new Vector<Properties>(m_configsets.getConfigurationSets());
                config_sets.value = new ConfigurationSet[cf.size()];
                for(int intIdx=0; intIdx<cf.size(); ++intIdx) {
                    if( config_sets.value[intIdx]==null ) config_sets.value[intIdx] = new ConfigurationSet();
                    toConfigurationSet(config_sets.value[intIdx], cf.elementAt(intIdx));
                }
                return config_sets.value;
            }
        } catch (Exception ex) {
            throw new InternalError("Configuration::get_configuration_sets()");
        }
    }

    /**
     * <p>[CORBA interface] ConfigurationSet を取得します。<br />
     *
     * このオペレーションは引数で指定された ConfigurationSet の ID に関連
     * 付けられた ConfigurationSet を返します。</p>
     *
     * @param config_id ConfigurationSet の識別子。
     * @return 引数により指定された ConfigurationSet。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public synchronized ConfigurationSet get_configuration_set(String config_id)
            throws NotAvailable, InternalError {
        if( config_id==null || config_id.equals("") ) throw new InternalError("ID is empty");
        // Originally getConfigurationSet raises InvalidParameter according to the
        // SDO specification. However, SDO's IDL lacks InvalidParameter.
        
        if(!m_configsets.haveConfig(config_id)) throw new InternalError("No such ConfigurationSet");
        
        final Properties configset = new Properties(m_configsets.getConfigurationSet(config_id));
        try{
            ConfigurationSet config = new ConfigurationSet();
            toConfigurationSet(config, configset);
            return config;
        } catch (Exception ex) {
            throw new InternalError("Configuration::get_configuration_set()");
        }
    }

    /**
     * <p>[CORBA interface] ConfigurationSet を設定します。<br />
     *
     * このオペレーションは指定された id の ConfigurationSet を更新します。</p>
     *
     * @param config_id 変更する ConfigurationSet の ID。
     * @param configuration_set 変更する ConfigurationSet そのもの。
     * @return ConfigurationSet が正常に更新できた場合は true。
     *         そうでなければ false を返す。
     *
     * @exception InvalidParameter config_id が null か ConfigurationSet
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean set_configuration_set_values(String config_id, ConfigurationSet configuration_set) 
            throws InvalidParameter, NotAvailable, InternalError {
        if( config_id==null || config_id.equals("") ) throw new InvalidParameter("ID is empty.");
        try {
            Properties conf = new Properties(config_id);
            toProperties(conf, configuration_set);
            return m_configsets.setConfigurationSetValues(config_id, conf);
        } catch( Exception ex) {
            throw new InternalError("Configuration::set_configuration_set_values()");
        }
    }

    /**
     * <p>[CORBA interface] アクティブな ConfigurationSet を取得します。<br />
     *
     * このオペレーションは当該SDOの現在アクティブな ConfigurationSet を返します。
     * (もしSDOの現在の設定が予め定義された ConfigurationSet により設定されて
     * いるならば。)
     * ConfigurationSet は以下の場合にはアクティブではないものとみなされます。
     *
     * <ol>
     * <li>現在の設定が予め定義された ConfigurationSet によりセットされていない</li>
     * <li>SDO の設定がアクティブになった後に変更された</li>
     * <li>SDO を設定する ConfigurationSet が変更された</li>
     * <ol>
     * これらの場合には、空の ConfigurationSet が返されます。</p>
     *
     * @return 現在アクティブな ConfigurationSet。
     *
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public ConfigurationSet get_active_configuration_set() 
        throws NotAvailable, InternalError {
        if( !m_configsets.isActive() ) throw new NotAvailable();
        
        try {
            synchronized (m_configsets) {
                ConfigurationSet config = new ConfigurationSet();
                toConfigurationSet(config, m_configsets.getActiveConfigurationSet());
                return config;
            }
        } catch (Exception ex) {
            throw new InternalError("Configuration::get_active_configuration_set()");
        }
    }

    /**
     * <p>[CORBA interface] ConfigurationSet を追加します。<br />
     *
     * ConfigurationProfile に ConfigurationSet を追加するオペレーション。</p>
     *
     * @param configuration_set 追加される ConfigurationSet。
     * @return オペレーションが成功したかどうか。
     *
     * @exception InvalidParameter 引数 "configuration_set" が null である、
     *            もしくは、引数で指定された ConfigurationSet が存在しない。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean add_configuration_set(ConfigurationSet configuration_set)
            throws InvalidParameter, NotAvailable, InternalError {
        try{
            synchronized (m_configsets) {
                final String config_id = new String(configuration_set.id);
                Properties config = new Properties(config_id);
                toProperties(config, configuration_set);
                return m_configsets.addConfigurationSet(config);

            }
        } catch(Exception ex) {
            throw new InternalError("Configuration::add_configuration_set()"); 
        }
    }

    /**
     * <p>[CORBA interface] ConfigurationSet を削除します。<br />
     *
     * ConfigurationProfile から ConfigurationSet を削除します。</p>
     *
     * @param config_id 削除する ConfigurationSet の id。
     * @return オペレーションが成功したかどうか。
     *
     * @exception InvalidParameter 引数 "configurationSetID" が null である、
     *            もしくは、引数で指定された ConfigurationSet が存在しない。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean remove_configuration_set(String config_id)
            throws InvalidParameter, NotAvailable, InternalError {
        if( config_id==null || config_id.equals(""))
            throw new InvalidParameter("ID is empty.");
        try {
            synchronized (m_configsets) {
                return m_configsets.removeConfigurationSet(config_id);
            }
        } catch (Exception ex) {
            throw new InternalError("Configuration::remove_configuration_set()");
        }
    }

    /**
     * <p>[CORBA interface] ConfigurationSet をアクティブ化します。<br />
     *
     * ConfigurationProfile に格納された ConfigurationSet のうち一つを
     * アクティブにします。
     * このオペレーションは特定の ConfigurationSet をアクティブにします。
     * すなわち、SDO のコンフィギュレーション・プロパティがその格納されている
     * ConfigurationSet により設定されるプロパティの値に変更されます。
     * 指定された ConfigurationSet の値がアクティブ・コンフィギュレーション
     * にコピーされるということを意味します。</p>
     *
     * @param config_id アクティブ化する ConfigurationSet の id。
     * @return オペレーションが成功したかどうか。
     *
     * @exception InvalidParameter 引数 "config_id" が null である、もしくは
     *            引数で指定された ConfigurationSet が存在しない。
     * @exception NotAvailable SDOは存在するが応答がない。
     * @exception InternalError 内部的エラーが発生した。
     */
    public boolean activate_configuration_set(String config_id)
            throws InvalidParameter, NotAvailable, InternalError {
        if( config_id==null || config_id.equals(""))
            throw new InvalidParameter("ID is empty.");
        try {
            m_configsets.activateConfigurationSet(config_id);
        } catch (Exception ex) {
            throw new InternalError("Configuration::activate_configuration_set()");
        }
        return false;
    }

    /**
     * <p>オブジェクト参照を取得します。</p>
     *
     * @return オブジェクト参照
     */
    public Configuration getObjRef() {
        return m_objref;
    }

    /**
     * <p>[CORBA interface] SDO の DeviceProfile を取得します。</p>
     *
     * @return SDOのDeviceProfile。
     */
    public final DeviceProfile getDeviceProfile() {
      return m_deviceProfile;
    }

    /**
     * <p>[CORBA interface] SDO の 全DeviceProfile を取得します。</p>
     *
     * @return SDOのDeviceProfile。
     */
    public final ServiceProfileListHolder getServiceProfiles() {
      return m_serviceProfiles;
    }

    /**
     * <p>[CORBA interface] SDO の ServiceProfile を取得します。
     *   指定したIDのServiceProfileが存在しない場合は，空のServiceProfileを返します。</p>
     *
     * @param id 取得対象 ServiceProfile の id
     * @return SDOのServiceProfile
     */
    public final ServiceProfile getServiceProfile(final String id) {
        for(int index=0; index<m_serviceProfiles.value.length; index++ ) {
            if(id.equals(m_serviceProfiles.value[index].id)) {
                return m_serviceProfiles.value[index];
            }
        }
        return new ServiceProfile();
    }
    
    /**
     * <p>[CORBA interface] 設定された全Organizationを取得します。</p>
     *
     * @return Organizationリスト
     */
    public final OrganizationListHolder getOrganizations() {
      return m_organizations;
    }
    /**
     * <p>オブジェクト参照</p>
     */
    protected Configuration m_objref;
    /**
     * <p>DeviceProfile</p>
     */
    protected DeviceProfile m_deviceProfile = new DeviceProfile();
    /**
     * <p>ServiceProfile リスト</p>
     */
    protected ServiceProfileListHolder m_serviceProfiles;
    /**
     * <p>Parameter リスト</p>
     */
    protected ParameterListHolder m_parameters;
    /**
     * <p>コンフィギュレーションセット情報</p>
     */
    protected ConfigAdmin m_configsets;
    /**
     * <p>Organization リスト</p>
     */
    protected OrganizationListHolder m_organizations;
}

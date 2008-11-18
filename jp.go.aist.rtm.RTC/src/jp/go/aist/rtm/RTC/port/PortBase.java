package jp.go.aist.rtm.RTC.port;

import java.util.UUID;

import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;

import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.ConnectorProfileListHolder;
import RTC.Port;
import RTC.PortHelper;
import RTC.PortInterfacePolarity;
import RTC.PortInterfaceProfile;
import RTC.PortInterfaceProfileListHolder;
import RTC.PortListHolder;
import RTC.PortPOA;
import RTC.PortProfile;
import RTC.RTObject;
import RTC.ReturnCode_t;


import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;
import jp.go.aist.rtm.RTC.util.ConnectorProfileFactory;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.POAUtil;
import jp.go.aist.rtm.RTC.util.PortProfileFactory;
import jp.go.aist.rtm.RTC.util.equalFunctor;
import jp.go.aist.rtm.RTC.util.operatorFunc;


/*!
 * @if jp
 * @class PortBase
 * @brief Port の基底クラス
 *
 * RTC::Port の基底となるクラス。
 * RTC::Port はほぼ UML Port の概念を継承しており、ほぼ同等のものとみなす
 * ことができる。RT コンポーネントのコンセプトにおいては、
 * Port はコンポーネントに付属し、コンポーネントが他のコンポーネントと相互作用
 * を行う接点であり、通常幾つかのインターフェースと関連付けられる。
 * コンポーネントは Port を通して外部に対しインターフェースを提供または要求
 * することができ、Portはその接続を管理する役割を担う。
 * <p>
 * Port の具象クラスは、通常 RT コンポーネントインスタンス生成時に同時に
 * 生成され、提供・要求インターフェースを登録した後、RT コンポーネントに
 * 登録され、外部からアクセス可能な Port として機能することを想定している。
 * <p>
 * RTC::Port は CORBA インターフェースとして以下のオペレーションを提供する。
 *
 * - get_port_profile()
 * - get_connector_profiles()
 * - get_connector_profile()
 * - connect()
 * - notify_connect()
 * - disconnect()
 * - notify_disconnect()
 * - disconnect_all()
 *
 * このクラスでは、これらのオペレーションの実装を提供する。
 * <p>
 * これらのオペレーションのうち、get_port_profile(), get_connector_profiles(),
 * get_connector_profile(), connect(), disconnect(), disconnect_all() は、
 * サブクラスにおいて特に振る舞いを変更する必要がないため、オーバーライド
 * することは推奨されない。
 * <p>
 * notify_connect(), notify_disconnect() については、サブクラスが提供・要求
 * するインターフェースの種類に応じて、振る舞いを変更する必要が生ずる
 * かもしれないが、これらを直接オーバーライドすることは推奨されず、
 * 後述の notify_connect(), notify_disconnect() の項においても述べられる通り
 * これらの関数に関連した protected 関数をオーバーライドすることにより
 * 振る舞いを変更することが推奨される。
 *
 * @else
 *
 *
 * @endif
 */
/**
 * <p>Portサーバントのベース実装となるクラスです。</p>
 * 
 * <p>Portは、UMLにおけるポートの概念をほぼ継承しており、ほぼ同等のものとみなすことができます。
 * RTコンポーネントのコンセプトにおいては、Portはコンポーネントに付属するものであり、
 * コンポーネントが他のコンポーネントと相互作用を行う接点であり、
 * 通常は幾つかのインターフェースと関連付けられます。</p>
 * 
 * <p>コンポーネントはPortを通して外部に対してインターフェースを提供または要求
 * することができ、Portはその接続を管理する役割を担っています。</p>
 * 
 * <p>Portの具象クラスは、通常はRTコンポーネントインスタンス生成時に同時に生成され、
 * 提供・要求インターフェースを登録した後、RTコンポーネントに登録され、
 * 外部からアクセス可能なPortとして機能することを想定しています。</p>
 *
 * <p>Portは、CORBAインターフェースとして以下のオペレーションを提供します。
 * 
 * <ul>
 * <li>get_port_profile()</li>
 * <li>get_connector_profiles()</li>
 * <li>get_connector_profile()</li>
 * <li>connect()</li>
 * <li>notify_connect()</li>
 * <li>disconnect()</li>
 * <li>notify_disconnect()</li>
 * <li>disconnect_all()</li>
 * </ul>
 * 
 * このクラスでは、これらのオペレーションの実装を提供しています。</p>
 * 
 * <p>上記のオペレーションのうち、get_port_profile(), get_connector_profiles(),
 * get_connector_profile(), connect(), disconnect(), disconnect_all()は、
 * サブクラスにおいて特に振る舞いを変更する必要がないため、オーバーライドすることは推奨されません。</p>
 * 
 * <p>notify_connect(), notify_disconnect()については、サブクラスが提供・要求するインターフェースの
 * 種類に応じて、振る舞いを変更する必要が生ずるかもしれませんが、これらを直接オーバーライドすることは
 * 推奨されず、後述のnotify_connect(), notify_disconnect()の項にも記述されている通りに、
 * これらのメソッドに関連したprotectedメソッドをオーバーライドすることにより振る舞いを変更することが
 * 推奨されます。</p>
 */
public abstract class PortBase extends PortPOA {
    
    /**
     * <p>本コンストラクタでは、オブジェクトの初期化処理を行うと同時に、
     * 自身をCORBAオブジェクトとして活性化します。さらに、自分自身のPortProfileの
     * port_refメンバに、自身のCORBAオブジェクト参照を格納します。</p>
     * 
     * @param name ポート名
     */
    public PortBase(final String name) {
        this.m_profile.name = new String(name);
        this.m_profile.owner = null;
        this.m_profile.interfaces = new PortInterfaceProfile[0];
        this.m_profile.connector_profiles = new ConnectorProfile[0];
        this.m_profile.properties = new NameValue[0];
        this.m_objref = PortHelper.narrow(this._this()._duplicate());
        this.m_profile.port_ref = this.m_objref;
    }

    /**
     * <p>当該PortのCORBAオブジェクト参照を取得します。</p>
     * 
     * @return 当該PortのCORBAオブジェクト参照
     */
    public Port _this() {
        if (this.m_objref == null) {
            try {
                this.m_objref = PortHelper.narrow(POAUtil.getRef(this));
                
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        
        return this.m_objref;
    }

    /**
     * <p>デフォルトコンストラクタです。ポート名には空文字列が割り当てられます。</p>
     */
    public PortBase() {
        this("");
    }

    /**
     * <p>PortProfileを取得します。なお、PortProfileは次のメンバを持っています。
     * <ul>
     * <li>name [String型] Port名</li>
     * <li>interfaces [PortInterfaceProfile配列型] Port が保持するPortInterfaceProfileのシーケンス</li>
     * <li>port_ref [Port型] Port自身のCORBAオブジェクト参照</li>
     * <li>connector_profile [ConnectorProfile配列型] Portが現在保持するConnectorProfileのシーケンス</li>
     * <li>owner [RTObject型] このPortを所有するRTObjectのCORBAオブジェクト参照</li>
     * <li>properties [NameValue配列型] その他のプロパティ</li>
     * </ul>
     * </p>
     * 
     * @return 本ポートに関するPortProfileオブジェクト
     */
    public PortProfile get_port_profile() {
        synchronized (this.m_profile) {
            return PortProfileFactory.clone(this.m_profile);
        }
    }
    
    /**
     * <p>PortProfileを取得します。</p>
     * 
     * @return 本ポートに関するPortProfile
     */
    public final PortProfile getPortProfile() {
        return m_profile;
    }

    /**
     * <p>ConnectorProfileリストを取得します。
     * ConnectorProfileはポート間の接続プロファイル情報を保持するクラスであり、
     * 接続時にポート間で情報交換を行い、関連するすべてのポートで同一の値が保持されます。</p>
     * 
     * <p>ConnectorProfileは以下のメンバを保持しています。
     * <ul>
     * <li>name [String型] コネクタ名</li>
     * <li>connector_id [String型] ユニークなコネクタID</li>
     * <li>ports [Port配列型] このコネクタに関連するPortのCORBAオブジェクト参照のシーケンス
     * <li>properties [NameValue配列型] その他のプロパティ</li>
     * </ul>
     * </p>
     *
     * @return このポートに関連するConnectorProfileオブジェクトの配列
     */
    public RTC.ConnectorProfile[] get_connector_profiles() {

        synchronized (this.m_profile) {

            int length = this.m_profile.connector_profiles.length;

            RTC.ConnectorProfile[] conn_prof = new RTC.ConnectorProfile[length];
            for (int i = 0; i < length; i++) {
                conn_prof[i] = this.m_profile.connector_profiles[i];
            }

            return conn_prof;
        }
    }

    /**
     * <p>指定されたコネクタIDに対応する接続プロファイルを取得します。</p>
     * 
     * @param connector_id コネクタID
     * @return 指定されたコネクタIDに対応する接続プロファイル
     */
    public ConnectorProfile get_connector_profile(final String connector_id) {

        synchronized (this.m_profile) {

            ConnectorProfileListHolder holder =
                new ConnectorProfileListHolder(this.m_profile.connector_profiles);
            int index = CORBA_SeqUtil.find(holder, new find_conn_id(connector_id));
            
            if (index < 0) {
                ConnectorProfile conn_prof = ConnectorProfileFactory.create();
                return conn_prof;
            }
            ConnectorProfile org_conn_prof = this.m_profile.connector_profiles[index];
            ConnectorProfile conn_prof = new ConnectorProfile(
                    org_conn_prof.name,
                    org_conn_prof.connector_id,
                    org_conn_prof.ports,
                    org_conn_prof.properties);
            
            return conn_prof;
        }
    }

    /**
     * <p>ポート間の接続を行います。指定された接続プロファイルにしたがってポート間の接続を確立します。</p>
     * 
     * <p>アプリケーションプログラム側は、幾つかのコンポーネントが持つ複数のポート間を接続したい場合、
     * 適切な値を設定した接続プロファイルを引数として渡すことにより、関連するポート間の接続を確立できます。</p>
     * 
     * <p>本メソッドの呼び出しにあたっては、ConnectorProfileオブジェクトのメンバのうち、
     * 次のものについて値を設定しておかねばなりません。
     * <ul>
     * <li>name</li>
     * <li>ports</li>
     * <li>properties</li>
     * </ul>
     * </p>
     * 
     * @param connector_profile 接続プロファイル
     * @return ReturnCode_t型による戻り値
     */
    public ReturnCode_t connect(ConnectorProfileHolder connector_profile) {

        if (isEmptyId(connector_profile.value)) {
            
            // "connector_id" stores UUID which is generated at the initial Port
            // in connection process.
            setUUID(connector_profile);
            assert (! isExistingConnId(connector_profile.value.connector_id));
        }

        try {
            return connector_profile.value.ports[0].notify_connect(connector_profile);
        } catch(Exception ex) {
            return ReturnCode_t.BAD_PARAMETER;
        }
    }

    /**
     * <p>ポートの接続通知を行います。本メソッドは、ポート間で接続が行われる際に、
     * ポート間で内部的に呼び出されます。</p>
     * 
     * @param connector_profile 接続プロファイル
     * @return ReturnCode_t型の戻り値
     */
    public ReturnCode_t notify_connect(ConnectorProfileHolder connector_profile) {

        // publish owned interface information to the ConnectorProfile
        ReturnCode_t retval;
        retval = publishInterfaces(connector_profile);
        if (! ReturnCode_t.RTC_OK.equals(retval)) {
            return retval;
        }

        // call notify_connect() of the next Port
        retval = connectNext(connector_profile);
        if (! ReturnCode_t.RTC_OK.equals(retval)) {
            return retval;
        }

        // subscribe interface from the ConnectorProfile's information
        retval = subscribeInterfaces(connector_profile);
        if (! ReturnCode_t.RTC_OK.equals(retval)) {
            // cleanup this connection for downstream ports
            notify_disconnect(connector_profile.value.connector_id);
            return retval;
        }

        // update ConnectorProfile
        int index = findConnProfileIndex(connector_profile.value.connector_id);
        if (index < 0) {
            ConnectorProfileListHolder holder =
                new ConnectorProfileListHolder(this.m_profile.connector_profiles);
            CORBA_SeqUtil.push_back(holder, connector_profile.value);
            this.m_profile.connector_profiles = holder.value;

        } else {
            this.m_profile.connector_profiles[index] = connector_profile.value;
        }

        return retval;
    }

    /**
     * <p>ポートの接続を解除します。このメソッドは、接続確立時に接続に対して与えられるコネクタIDに
     * 対応するピア・ポートとの接続を解除します。</p>
     * 
     * @param connector_id コネクタID
     * @return ReturnCode_t型の戻り値
     */
    public ReturnCode_t disconnect(final String connector_id) {

        // find connector_profile
        if (! isExistingConnId(connector_id)) {
            return ReturnCode_t.BAD_PARAMETER;
        }

        int index = findConnProfileIndex(connector_id);
        ConnectorProfile org_conn_prof = this.m_profile.connector_profiles[index];
        ConnectorProfile prof = new ConnectorProfile(
                org_conn_prof.name,
                org_conn_prof.connector_id,
                org_conn_prof.ports,
                org_conn_prof.properties);

        return prof.ports[0].notify_disconnect(connector_id);
    }

    /**
     * <p>ポートの接続解除通知を行います。本メソッドは、ポート間の接続解除が行われる際に、
     * ポート間で内部的に呼び出されます。</p>
     * 
     * @param connector_id コネクタID
     * @return ReturnCode_t型の戻り値
     */
    public ReturnCode_t notify_disconnect(final String connector_id) {

        // The Port of which the reference is stored in the beginning of
        // ConnectorProfile's PortList is master Port.
        // The master Port has the responsibility of disconnecting all Ports.
        // The slave Ports have only responsibility of deleting its own
        // ConnectorProfile.

        // find connector_profile
        if (! isExistingConnId(connector_id)) {
            return ReturnCode_t.BAD_PARAMETER;
        }

        int index = findConnProfileIndex(connector_id);

        ConnectorProfile org_conn_prof = this.m_profile.connector_profiles[index];
        ConnectorProfile prof = new ConnectorProfile(
                org_conn_prof.name,
                org_conn_prof.connector_id,
                org_conn_prof.ports,
                org_conn_prof.properties);

        ReturnCode_t retval = disconnectNext(prof);
        unsubscribeInterfaces(prof);

        ConnectorProfileListHolder holder =
            new ConnectorProfileListHolder(this.m_profile.connector_profiles);
        CORBA_SeqUtil.erase(holder, index);
        this.m_profile.connector_profiles = holder.value;
        
        return retval;
    }

    /**
     * <p>当該ポートに関連したすべての接続を解除します。</p>
     * 
     * @return ReturnCode_t型の戻り値
     */
    public ReturnCode_t disconnect_all() {

        synchronized (this.m_profile) {

            ConnectorProfileListHolder holder =
                new ConnectorProfileListHolder(this.m_profile.connector_profiles);
            disconnect_all_func f = (disconnect_all_func) CORBA_SeqUtil.for_each(
                    holder, new disconnect_all_func(this));
            this.m_profile.connector_profiles = holder.value;
            
            return f.m_return_code;
        }
    }

    /**
     * <p>ポート名を設定します。指定されたポート名は、PortProfileのnameメンバに設定されます。</p>
     * 
     * @param name ポート名
     */
    public void setName(final String name) {

        synchronized (this.m_profile) {

            this.m_profile.name = new String(name);
        }
    }

    /**
     * <p>当該ポートが保持しているPortProfileオブジェクトを取得します。</p>
     * 
     * @return 当該ポートが保持しているPortProfileオブジェクト
     */
    public final PortProfile getProfile() {

        synchronized (this.m_profile) {

            return this.m_profile;
        }
    }

    /**
     * <p>指定されたポートCORBAオブジェクト参照を設定します。</p>
     * <p>本メソッドは、当該ポート自身のCORBAオブジェクト参照を設定するために用います。
     * 当該ポートのCORBAオブジェクト参照以外は設定しないでください。</p>
     * 
     * @param port_ref 当該ポートのCORBAオブジェクト参照
     */
    public void setPortRef(Port port_ref) {

        synchronized (this.m_profile) {

            m_profile.port_ref = port_ref;
        }
    }

    /**
     * <p>当該ポートのPortProfileが保持している、当該ポート自身のCORBAオブジェクト参照を取得します。</p>
     * 
     * @return 当該ポートのCORBAオブジェクト参照
     */
    public Port getPortRef() {

        synchronized (this.m_profile) {

            return m_profile.port_ref;
        }
    }

    /**
     * <p>当該ポートのオーナーRTObjectを設定します。
     * 指定されたRTObjectオブジェクトが、PortProfileのownerメンバに設定されます。</p>
     * 
     * @param owner 当該ポートを所有するRTObjectのCORBAオブジェクト参照
     */
    public void setOwner(RTObject owner) {

        synchronized (this.m_profile) {

            this.m_profile.owner = owner;
        }
    }

    /**
     * <p>Interface情報を公開します。
     * このメソッドは、notify_connect()処理シーケンスの始めに呼び出されるテンプレートメソッドです。</p>
     * 
     * <p>notify_connect()内では、以下の順にテンプレートメソッドが呼び出されて接続処理が行われます。
     * <ol>
     * <li>publishInterfaces()</li>
     * <li>connectNext()</li>
     * <li>subscribeInterfaces()</li>
     * <li>updateConnectorProfile()</li>
     * </ol>
     * 
     * <p>具象Portクラスでは、このメソッドをオーバーライドし、引数として与えられた
     * ConnectorProfileに従って処理を行い、パラメータが不適切であれば、ReturnCode_t型の
     * エラーコードを返します。</p>
     * 
     * <p>通常、publishInterafaces()内においては、このポートに属するインターフェースに関する情報を
     * ConnectorProfileに対して適切に設定し、他のポートに通知しなければなりません。
     * また、このメソッドが呼び出される段階では、他のポートのInterfaceに関する情報は
     * すべては含まれていないので、他のポートのInterfaceを取得する処理はsubscribeInterfaces()内で
     * 行われるべきです。</p>
     * 
     * <p>このメソッドは、新規のコネクタIDに対しては接続の生成が、既存のコネクタIDに対しては更新が
     * 適切に行われる必要があります。</p>
     *
     * @param connector_profile 接続プロファイル
     * @return ReturnCode_t型の戻り値
     */
    protected abstract ReturnCode_t publishInterfaces(ConnectorProfileHolder connector_profile);

    /**
     * <p>当該ポートの次のポートに対して接続通知を行います。</p>
     * 
     * <p>ConnectorProfile内に設定されているPortのシーケンスを調べて、
     * 当該Portの次のPortを特定し、そのPortに対してnotify_connect()を呼び出します。</p>
     * 
     * @param connector_profile 接続プロファイル
     */
    protected ReturnCode_t connectNext(ConnectorProfileHolder connector_profile) {

        PortListHolder portsHolder = new PortListHolder(connector_profile.value.ports);
        int index = CORBA_SeqUtil.find(portsHolder, new find_port_ref(this.m_profile.port_ref));
        connector_profile.value.ports = portsHolder.value;
        
        if (index < 0) {
            return ReturnCode_t.BAD_PARAMETER;
        }
        
        if (++index < connector_profile.value.ports.length) {
            Port p = connector_profile.value.ports[index];
            ReturnCode_t rc = p.notify_connect(connector_profile);
            return rc;
        }
        
        return ReturnCode_t.RTC_OK;
    }

    /**
     * <p>当該ポートの次のポートに対して接続解除通知を行います。</p>
     * 
     * <p>ConnectorProfile内に設定されているPortのシーケンスを調べて、
     * 当該Portの次のPortを特定し、そのPortに対してnotify_disconnect()を呼び出します。</p>
     * 
     * @param connector_profile 接続プロファイル
     */
    protected ReturnCode_t disconnectNext(ConnectorProfile connector_profile) {

        PortListHolder holder = new PortListHolder(connector_profile.ports);
        int index = CORBA_SeqUtil.find(holder, new find_port_ref(this.m_profile.port_ref));
        connector_profile.ports = holder.value;
        
        if (index < 0) {
            return ReturnCode_t.BAD_PARAMETER;
        }
        
        if (++index < connector_profile.ports.length) {
            Port p = connector_profile.ports[index];
            return p.notify_disconnect(connector_profile.connector_id);
        }

        unsubscribeInterfaces(connector_profile);
        
        return ReturnCode_t.RTC_OK;
    }

    /**
     * <p>Interface 情報を公開します。</p>
     *
     * <p>このメソッドは、notify_connect()処理シーケンスの中間に呼び出されるテンプレートメソッドです。
     * notify_connect()では、
     * <ol>
     * <li>publishInterfaces()</li>
     * <li>connectNext()</li>
     * <li>subscribeInterfaces()</li>
     * <li>updateConnectorProfile()</li>
     * </ol>
     * の順にprotectedメソッドが呼び出されて接続処理が行われます。
     * </p>
     * 
     * <p>このメソッドの処理は、新規のconnector_idに対しては接続の生成が適切に行われる必要があります。<br />
     * また、既存のconnector_idに対しては更新が適切に行われる必要があります。</p>
     *
     * @param connector_profile 接続プロファイル情報
     * @return ReturnCode_t 戻り値
     */
    protected abstract ReturnCode_t subscribeInterfaces(
            final ConnectorProfileHolder connector_profile);

    /**
     * <p>Interfaceの接続を解除します。</p>
     *
     * <p>このメソッドは、notify_disconnect()処理シーケンスの終わりに呼び出されるテンプレートメソッドです。
     * notify_disconnect()では、
     * <ol>
     * <li>disconnectNext()</li>
     * <li>unsubscribeInterfaces()</li>
     * <li>eraseConnectorProfile()</li>
     * </ol>
     * の順にprotectedメソッドが呼び出されて接続解除処理が行われます。
     * </p>
     * 
     * <p>具象クラスでは、このメソッドをオーバーライドし、引数として
     * 与えられた ConnectorProfile に従い接続解除処理を行います。</p>
     *
     * @param connector_profile 接続プロファイル情報
     */
    protected abstract void unsubscribeInterfaces(final ConnectorProfile connector_profile);

    /**
     * <p>指定されたConnectorProfileオブジェクト内のconnector_idメンバが空かどうか判定します。</p>
     * 
     * @return 空であればtrueを、さもなくばfalseを返します。
     */
    protected boolean isEmptyId(final ConnectorProfile connector_profile) {
        return connector_profile.connector_id.length() == 0;
    }

    /**
     * <p>UUIDを生成します。<p>
     * 
     * @return 生成されたUUID
     */
    protected String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * <p>UUIDを生成し、指定されたConnectorProfileオブジェクトのconnector_idメンバに設定します。</p>
     * 
     * @param connector_profile 設定先となるConnectorProfileオブジェクト
     */
    protected void setUUID(ConnectorProfileHolder connector_profile) {
        connector_profile.value.connector_id = getUUID();
        assert (connector_profile.value.connector_id.length() != 0);
    }

    /**
     * <p>指定された接続IDを持つ接続プロファイルが、当該ポートのPortProfileに設定されている
     * ConnectorProfileオブジェクトシーケンス内に存在するかどうかを判定します。</p>
     * 
     * @return 存在すればtrueを、さもなくばfalseを返します。
     */
    protected boolean isExistingConnId(final String id) {
        ConnectorProfileListHolder holder =
            new ConnectorProfileListHolder(this.m_profile.connector_profiles);
        int index = CORBA_SeqUtil.find(holder, new find_conn_id(id));
        this.m_profile.connector_profiles = holder.value;
        
        return (index >= 0);
    }
    
    /**
     * <p>指定された接続IDを持つ接続プロファイルが、当該ポートのPortProfileに設定されている
     * ConnectorProfileオブジェクトシーケンス内に存在するかどうかを調べ、存在する場合は
     * そのConnectorProfileオブジェクトが取得されます。</p>
     * 
     * @param id 接続ID
     * @return 指定した接続IDを持つConnectorProfileオブジェクトが見つかった場合は、
     * そのオブジェクトを返します。<br />
     * 見つからない場合は、空のConnectorProfileオブジェクトを返します。
     */
    protected ConnectorProfile findConnProfile(final String id) {

        ConnectorProfileListHolder holder =
            new ConnectorProfileListHolder(this.m_profile.connector_profiles);
        int index = CORBA_SeqUtil.find(holder, new find_conn_id(id));
        this.m_profile.connector_profiles = holder.value;
        
        if (index < 0) {
            return ConnectorProfileFactory.create();
        }
        
        return this.m_profile.connector_profiles[index];
    }

    /**
     * <p>指定された接続IDを持つ接続プロファイルが、当該ポートのPortProfileに設定されている
     * ConnectorProfileオブジェクトシーケンス内に存在するかどうかを調べ、存在する場合は
     * そのConnectorProfileオブジェクトのシーケンス内でのインデクスが取得されます。</p>
     * 
     * @return 指定した接続IDを持つConnectorProfileオブジェクトが見つかった場合は、
     * そのオブジェクトのシーケンス内インデクスを返します。<br />
     * 見つからない場合は、-1を返します。
     */
    protected int findConnProfileIndex(final String id) {

        ConnectorProfileListHolder holder =
            new ConnectorProfileListHolder(this.m_profile.connector_profiles);
        int index = CORBA_SeqUtil.find(holder, new find_conn_id(id));
        this.m_profile.connector_profiles = holder.value;
        
        return index;
    }

    /**
     * <p>指定された接続プロファイルを、当該ポートのPortProfileに追加します。
     * 同一の接続IDを持つ接続プロファイルがすでにある場合は、その内容を上書きします。</p>
     * 
     * @param connector_profile 追加もしくは更新する接続プロファイル
     */
    protected void updateConnectorProfile(final ConnectorProfile connector_profile) {
        
        ConnectorProfileListHolder cprof_list =
            new ConnectorProfileListHolder(this.m_profile.connector_profiles);
        
        int index = CORBA_SeqUtil.find(
                cprof_list, new find_conn_id(connector_profile.connector_id));
        this.m_profile.connector_profiles = cprof_list.value;
        
        
        if (index < 0) {
            CORBA_SeqUtil.push_back(cprof_list, connector_profile);
            this.m_profile.connector_profiles = cprof_list.value;

        } else {
            this.m_profile.connector_profiles[index] = connector_profile;
        }
    }
    
    /**
     * <p>指定された接続IDに対応する接続プロファイルを、当該ポートのPortProfileから削除します。</p>
     * 
     * @param connector_id 削除対象となる接続プロファイルの接続ID
     * @return 削除された場合はtrueを返します。<br />
     * また、指定された接続IDの接続プロファイルが見つからない場合はfalseを返します。
     */
    protected boolean eraseConnectorProfile(final String connector_id) {
        
        synchronized (this.m_profile) {
            
            ConnectorProfileListHolder cprof_list =
                new ConnectorProfileListHolder(this.m_profile.connector_profiles);

            int index = CORBA_SeqUtil.find(cprof_list, new find_conn_id(connector_id));
            this.m_profile.connector_profiles = cprof_list.value;
            
            if (index < 0) {
                return false;
            }
            
            CORBA_SeqUtil.erase(cprof_list, index);
            this.m_profile.connector_profiles = cprof_list.value;
            
            return true;
        }
    }
    
    /**
     * <p>当該ポートが持つPortProfile内に含まれるPortInterfaceProfileメンバに、
     * 引数で指定された情報を追加します。この情報は、get_port_profile()メソッドによって
     * 得られるPortProfileにうち、PortInterfaceProfileの値を変更するのみであり、
     * 実際にインタフェースを提供したり要求したりする場合には、サブクラス側で
     * publishInterface(), subscribeInterface()などのメソッドを適切にオーバライドし、
     * インタフェースの提供や要求処理を行う必要があります。</p>
     * 
     * <p>なお、インタフェース（のインスタンス）名は、ポート内で一意でなければなりません。</p>
     *
     * @param instance_name インタフェースのインスタンス名
     * @param type_name インタフェースの型名称
     * @param polarity インタフェースの属性（提供インタフェースまたは要求インタフェース）
     * @return 正常に登録できた場合はtrueを返します。
     * また、同名のインタフェースがすでに登録済みの場合はfalseを返します。
     */
    protected boolean appendInterface(final String instance_name,
            final String type_name, PortInterfacePolarity polarity) {
        
        PortInterfaceProfileListHolder port_if_prof_list =
            new PortInterfaceProfileListHolder(this.m_profile.interfaces);
        
        int index = CORBA_SeqUtil.find(
                port_if_prof_list, new find_interface(instance_name, polarity));
        this.m_profile.interfaces = port_if_prof_list.value;
        
        if (index >= 0) {
            return false;
        }
        
        // setup PortInterfaceProfile
        PortInterfaceProfile prof = new PortInterfaceProfile(instance_name, type_name, polarity);
        CORBA_SeqUtil.push_back(port_if_prof_list, prof);
        this.m_profile.interfaces = port_if_prof_list.value;
        
        return true;
    }
    
    /**
     * <p>指定されたインスタンス名と属性を持つインタフェースを、当該ポートが持つPortProfile内の
     * PortInterfaceProfileから削除します。</p>
     * 
     * @param instance_name 削除対象インタフェースのインスタンス名
     * @param polarity 削除対象インタフェースの属性
     * @return 正常に削除できた場合はtrueを返します。
     * また、該当するインタフェースが見つからない場合はfalseを返します。
     */
    protected boolean deleteInterface(final String instance_name, PortInterfacePolarity polarity) {

        PortInterfaceProfileListHolder port_if_prof_list =
            new PortInterfaceProfileListHolder(this.m_profile.interfaces);

        int index = CORBA_SeqUtil.find(port_if_prof_list, new find_interface(instance_name, polarity));
        this.m_profile.interfaces = port_if_prof_list.value;
        
        if (index < 0) {
            return false;
        }
        
        CORBA_SeqUtil.erase(port_if_prof_list, index);
        this.m_profile.interfaces = port_if_prof_list.value;
        
        return true;
    }
    
    /**
     * <p>当該ポートが持つPortProfile内のpropertiesメンバに、
     * 指定されたキーと値を持つNameValueオブジェクトを追加します。</p>
     * 
     * @param key キー
     * @param value 値
     */
    protected void addProperty(final String key, short value) {
        NVListHolder holder = new NVListHolder(this.m_profile.properties);
        CORBA_SeqUtil.push_back(holder, NVUtil.newNV(key, value));
        this.m_profile.properties = holder.value;
    }
    
    /**
     * <p>当該ポートが持つPortProfile内のpropertiesメンバに、
     * 指定されたキーと値を持つNameValueオブジェクトを追加します。</p>
     * 
     * @param key キー
     * @param value 値
     */
    protected void addProperty(final String key, long value) {
        NVListHolder holder = new NVListHolder(this.m_profile.properties);
        CORBA_SeqUtil.push_back(holder, NVUtil.newNV(key, value));
        this.m_profile.properties = holder.value;
    }
    
    /**
     * <p>当該ポートが持つPortProfile内のpropertiesメンバに、
     * 指定されたキーと値を持つNameValueオブジェクトを追加します。</p>
     * 
     * @param key キー
     * @param value 値
     */
    protected void addProperty(final String key, float value) {
        NVListHolder holder = new NVListHolder(this.m_profile.properties);
        CORBA_SeqUtil.push_back(holder, NVUtil.newNV(key, value));
        this.m_profile.properties = holder.value;
    }

    /**
     * <p>当該ポートが持つPortProfile内のpropertiesメンバに、
     * 指定されたキーと値を持つNameValueオブジェクトを追加します。</p>
     * 
     * @param key キー
     * @param value 値
     */
    protected void addProperty(final String key, double value) {
        NVListHolder holder = new NVListHolder(this.m_profile.properties);
        CORBA_SeqUtil.push_back(holder, NVUtil.newNV(key, value));
        this.m_profile.properties = holder.value;
    }

    /**
     * <p>当該ポートが持つPortProfile内のpropertiesメンバに、
     * 指定されたキーと値を持つNameValueオブジェクトを追加します。</p>
     * 
     * @param key キー
     * @param value 値
     */
    protected void addProperty(final String key, String value) {
        NVListHolder holder = new NVListHolder(this.m_profile.properties);
        CORBA_SeqUtil.push_back(holder, NVUtil.newNV(key, value));
        this.m_profile.properties = holder.value;
    }

    /**
     * <p>当該ポートに関連付けられているPortProfileオブジェクトです。</p>
     */
    protected PortProfile m_profile = new PortProfile();

    /**
     * <p>当該ポートのCORBAオブジェクト参照です。</p>
     */
    protected Port m_objref;

    /**
     * <p>指定された接続IDを持つ接続プロファイルを検索するためのヘルパクラスです。</p>
     */
    protected class find_conn_id implements equalFunctor {
        /**
         * <p>コンストラクタです。</p>
         * 
         * @param connector_id 検索対象の接続ID
         */
        public find_conn_id(final String connector_id) {
            this.m_connector_id = connector_id;
        }
        
        /**
         * <p>検索対象の接続IDを持つ接続プロファイルか否かを判定します。</p>
         * 
         * @param elem 判定対象となるオブジェクト
         * @return 検索対象であればtrueを、さもなくばfalseを返します。
         */
        public boolean equalof(final Object elem) {
            return equalof((ConnectorProfile) elem);
        }
        
        /**
         * <p>検索対象の接続IDを持つ接続プロファイルか否かを判定します。</p>
         * 
         * @param cprof 判定対象となる接続プロファイル
         * @return 検索対象であればtrueを、さもなくばfalseを返します。
         */
        public boolean equalof(final ConnectorProfile cprof) {
            return this.m_connector_id.endsWith(cprof.connector_id);
        }
        
        public String m_connector_id;
    }
    
    /**
     * <p>指定されたPortオブジェクトと同じCORBAオブジェクト参照を持つPortオブジェクトを
     * 検索するためのヘルパクラスです。</p>
     */
    protected class find_port_ref implements equalFunctor {
        
        /**
         * <p>コンストラクタです。</p>
         * 
         * @param port 判定基準となるCORBAオブジェクト参照を持つPortオブジェクト
         */
        public find_port_ref(Port port) {
            this.m_port = port;
        }
        
        /**
         * <p>検索対象となるCORBAオブジェクト参照を持つPortか否かを判定します。</p>
         * 
         * @param elem 判定対象となるオブジェクト
         * @return 検索対象である場合はtrueを、さもなくばfalseを返します。
         */
        public boolean equalof(final Object elem) {
            return equalof((Port) elem);
        }
        
        /**
         * <p>検索対象となるCORBAオブジェクト参照を持つPortか否かを判定します。</p>
         * 
         * @param port 判定対象となるPortオブジェクト
         * @return 検索対象である場合はtrueを、さもなくばfalseを返します。
         */
        public boolean equalof(final Port port) {
            return this.m_port._is_equivalent(port);
        }
        
        public Port m_port;
    }
    
    /**
     * <p>ポート接続のためのヘルパクラスです。</p>
     */
    protected class connect_func implements operatorFunc {
        public Port m_port_ref;
        public ConnectorProfileHolder m_connector_profile;
        public ReturnCode_t m_return_code;
        
        /**
         * <p>コンストラクタです。</p>
         */
        public connect_func() {
        }

        /**
         * <p>コンストラクタです。</p>
         * 
         * @param p 接続を行うポート
         * @param prof コネクタ・プロファイル
         */
        public connect_func(Port p, ConnectorProfileHolder prof) {
            this.m_port_ref = p;
            this.m_connector_profile = prof;
            this.m_return_code = ReturnCode_t.RTC_OK;
        }

        /**
         * <p>接続を行います。</p>
         * 
         * @param elem 接続プロファイルオブジェクト
         */
        public void operator(Object elem) {
            if( ! this.m_port_ref._is_equivalent((Port)elem) ) {
                ReturnCode_t retval;
                retval = ((Port)elem).notify_connect(this.m_connector_profile);
                if( retval != ReturnCode_t.RTC_OK ) {
                    this.m_return_code = retval;
                }
            }
        }
        
    }

    /**
     * <p>ポート接続解除のためのヘルパクラスです。</p>
     */
    protected class disconnect_func implements operatorFunc {
        public Port m_port_ref;
        public ConnectorProfileHolder m_connector_profile;
        public ReturnCode_t m_return_code;
        
        /**
         * <p>コンストラクタです。</p>
         */
        public disconnect_func() {
            this.m_return_code = ReturnCode_t.RTC_OK;
        }

        /**
         * <p>コンストラクタです。</p>
         * 
         * @param p 接続を行うポート
         * @param prof コネクタ・プロファイル
         */
        public disconnect_func(Port p, ConnectorProfileHolder prof) {
            this.m_port_ref = p;
            this.m_connector_profile = prof;
            this.m_return_code = ReturnCode_t.RTC_OK;
        }

        /**
         * <p>接続解除を行います。</p>
         * 
         * @param elem 接続プロファイルオブジェクト
         */
        public void operator(Object elem) {
            if( ! this.m_port_ref._is_equivalent((Port)elem) ) {
                ReturnCode_t retval;
                retval = ((Port)elem).disconnect(this.m_connector_profile.value.connector_id);
                if( retval != ReturnCode_t.RTC_OK ) {
                    this.m_return_code = retval;
                }
            }
        }
        
    }
    /**
     * <p>ポート接続解除のためのヘルパクラスです。</p>
     */
    protected class disconnect_all_func implements operatorFunc {
        
        /**
         * <p>コンストラクタです。</p>
         * 
         * @param port 接続解除を行うポート
         */
        public disconnect_all_func(PortBase port) {
            this.m_return_code = ReturnCode_t.RTC_OK;
            this.m_port = port;
        }
        
        /**
         * <p>接続解除を行います。</p>
         * 
         * @param elem 接続プロファイルオブジェクト
         */
        public void operator(Object elem) {
            operator((ConnectorProfile) elem);
        }
        
        /**
         * <p>接続解除を行います。</p>
         * 
         * @param cprof 接続プロファイルオブジェクト
         */
        public void operator(ConnectorProfile cprof) {
            ReturnCode_t retval = this.m_port.disconnect(cprof.connector_id);
            if (! retval.equals(ReturnCode_t.RTC_OK)) {
                this.m_return_code = retval;
            }
        }
        
        public ReturnCode_t m_return_code;
        public PortBase m_port;
    }

    /**
     * <p>指定されたインスタンス名と属性を持つインタフェースを検索するためのヘルパクラスです。</p>
     */
    protected class find_interface implements equalFunctor {

        /**
         * <p>コンストラクタです。</p>
         * 
         * @param instance_name 検索対象インタフェースのインスタンス名
         * @param polarity 検索対象インタフェースの属性
         */
        public find_interface(final String instance_name, PortInterfacePolarity polarity) {
            this.m_instance_name = instance_name;
            this.m_polarity = polarity;
        }

        /**
         * <p>検索対象となるインタフェースか否かを判定します。</p>
         * 
         * @param elem 判定対象のオブジェクト
         * @return 検索対象のインタフェースであればtrueを、さもなくばfalseを返します。
         */
        public boolean equalof(final Object elem) {
            return equalof((PortInterfaceProfile) elem);
        }
        
        /**
         * <p>検索対象となるインタフェースか否かを判定します。</p>
         * 
         * @param prof 判定対象のPortInterfaceProfileオブジェクト
         * @return 検索対象のインタフェースであればtrueを、さもなくばfalseを返します。
         */
        public boolean equalof(final PortInterfaceProfile prof) {
            return this.m_instance_name.equals(prof.instance_name)
                && this.m_polarity.equals(prof.polarity);
        }

        private String m_instance_name;
        private PortInterfacePolarity m_polarity;
    }
}

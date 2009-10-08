package jp.go.aist.rtm.RTC.port;

import java.util.Vector;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import _SDOPackage.NVListHolder;
import RTC.ConnectorProfile;
import RTC.ConnectorProfileHolder;
import RTC.ReturnCode_t;

import jp.go.aist.rtm.RTC.PublisherBaseFactory;
import jp.go.aist.rtm.RTC.InPortProviderFactory;
import jp.go.aist.rtm.RTC.OutPortProviderFactory;
import jp.go.aist.rtm.RTC.InPortConsumerFactory;
import jp.go.aist.rtm.RTC.buffer.BufferBase;
import jp.go.aist.rtm.RTC.port.publisher.PublisherBase;
import jp.go.aist.rtm.RTC.util.Properties;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.StringUtil;
import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;

/**
 * <p>出力ポートのベース実装クラスです。
 * Publisherの登録やPublisherへのデータ更新通知などの実装を提供します。</p>
 */
public class OutPortBase extends PortBase {

    /**
     * <p>コンストラクタです。</p>
     * 
     * @param name ポート名
     * @param data_type
     */
    public OutPortBase(final String name,final String data_type) {
        this.m_name = name;

        rtcout.setLevel("PARANOID");

        rtcout.println(rtcout.PARANOID, "Port name: "+name);
        rtcout.println(rtcout.PARANOID, "setting port.port_type: DataOutPort");

        addProperty("port.port_type", "DataOutPort", String.class);

        rtcout.println(rtcout.PARANOID,
                       "setting dataport.data_type: "+data_type);
        addProperty("dataport.data_type", data_type, String.class);

        // publisher list
        PublisherBaseFactory<PublisherBase,String> factory 
            = PublisherBaseFactory.instance();
        String pubs = factory.getIdentifiers().toString();

        // blank characters are deleted for RTSE's bug
        pubs = pubs.trim();
        rtcout.println(rtcout.PARANOID,
                       "available subscription_type: "+pubs);
        addProperty("dataport.subscription_type", pubs, String.class);

        initConsumers();
        initProviders();
    }

    /**
     * <p> Initializing properties </p>
     *
     * <p> This operation initializes outport's properties </p>
     *
     */
    void init(Properties prop) {
        rtcout.println(rtcout.TRACE, "init()");

        rtcout.println(rtcout.TRACE, "given properties:");
        String str = new String();
        prop._dump(str,prop,0);
        rtcout.println(rtcout.PARANOID, str);
        m_properties.merge(prop);

        rtcout.println(rtcout.PARANOID, "updated properties:");
        str = "";
        m_properties._dump(str,m_properties,0);

        configure();

    }
    /**
     * <p>プロパティを取得する</p>
     */
    public Properties properties() {
        rtcout.println(rtcout.TRACE, "properties()");
        return m_properties;
    }

    /**
     * <p>ポート名を取得します。</p>
     * 
     * @return ポート名
     */
    public String name() {
        rtcout.println(rtcout.TRACE, "name() = "+this.m_name);
        return this.m_name;
    }
    /**
     * <p> Connector list </p>
     *
     * <p> This operation returns connector list </p>
     *
     * @return connector list
     */
    public final Vector<OutPortConnector> connectors(){
        rtcout.println(rtcout.TRACE, 
                       "connectors(): size = "+m_connectors.size());
        return m_connectors;
    }
    /**
     * <p> ConnectorProfile list </p>
     * 
     * <p> This operation returns ConnectorProfile list </p>
     *
     * @return connector list
     */
    public Vector<ConnectorBase.Profile> getConnectorProfiles(){
        rtcout.println(rtcout.TRACE, 
                       "getConnectorProfiles(): size = "+m_connectors.size());
        Vector<ConnectorBase.Profile> profs 
            = new Vector<ConnectorBase.Profile>();
        for (int i=0, len=m_connectors.size(); i < len; ++i) {
            profs.add(m_connectors.elementAt(i).profile());
        }
        return profs;
    }
    /**
     * <p> ConnectorId list </p>
     *
     * <p> This operation returns ConnectorId list </p>
     *
     * @return connector list
     */
    public Vector<String> getConnectorIds(){
        Vector<String> ids = new Vector<String>();
        for (int i=0, len=m_connectors.size(); i < len; ++i) {
            ids.add(m_connectors.elementAt(i).id());
        }
        rtcout.println(rtcout.TRACE, 
                       "getConnectorIds(): "+ids.toString());
        return ids;
    }
    /**
     * <p> Connector name list </p>
     *
     * <p> This operation returns Connector name list </p>
     *
     * @return connector name list
     *
     */
    public Vector<String> getConnectorNames(){
        Vector<String> names = new Vector<String>();
        for (int i=0, len=m_connectors.size(); i < len; ++i) {
            names.add(m_connectors.elementAt(i).name());
        }
        rtcout.println(rtcout.TRACE, 
                       "getConnectorNames(): "+names.toString());
        return names;
    }
    /**
     * <p> Getting ConnectorProfile by name </p>
     *
     * <p> This operation returns ConnectorProfile specified by name </p>
     *
     * @param id Connector ID
     * @param prof ConnectorProfile
     * @return false specified ID does not exi
     *
     */
    public boolean getConnectorProfileById(final String id,
                                 ConnectorBase.Profile prof) {
        rtcout.println(rtcout.TRACE, 
                       "getConnectorProfileById(id = "+id+")");

        String sid = id;
        for (int i=0, len=m_connectors.size(); i < len; ++i) {
            if (sid.equals(m_connectors.elementAt(i).id())) {
                prof = m_connectors.elementAt(i).profile();
                return true;
            }
        }
        return false;
    }
    /**
     * <p> Getting ConnectorProfile by name </p>
     *
     * <p> This operation returns ConnectorProfile specified by name </p>
     *
     * @param name 
     * @param prof ConnectorProfile
     * @return false specified name does not exist
     *
     */
    public boolean getConnectorProfileByName(final String name,
                                   ConnectorBase.Profile prof) {
        rtcout.println(rtcout.TRACE, 
                       "getConnectorProfileByNmae(name = "+name+")");

        String sname = name;
        for (int i=0, len=m_connectors.size(); i < len; ++i) {
            if (sname.equals(m_connectors.elementAt(i).name())) {
                prof = m_connectors.elementAt(i).profile();
                return true;
              }
          }
        return false;
    }
    /** 
     * <p> Publish interface profile </p>
     *
     * <p> This operation publish interface profiles of this OutPort
     * to DataOutPort. This operation should be called from DataOutPort. </p>
     *
     * @param properties itnerface profile
     */
    public boolean publishInterfaceProfiles(NVListHolder properties) {
        rtcout.println(rtcout.TRACE, "publishInterfaceProfiles()");

        Iterator it = m_providers.iterator();
        while(it.hasNext()){
            OutPortProvider provider = (OutPortProvider)it.next();
            provider.publishInterfaceProfile(properties);
        }
        return true;
    }
    /**
     * <p> onConnect </p> 
     */
    public void onConnect(final String id, PublisherBase publisher){
    };
    /**
     *  <p> onDisconenct </p>
     */
    public void onDisconnect(final String id){
    };
    /**
     * <p>指定されたPublisherを、データ更新通知先として登録します。</p>
     * 
     * @param id 指定されたPublisherに割り当てるID
     * @param publisher 登録するPublisherオブジェクト
     */
/*
    public void attach(final String id, PublisherBase publisher) {
        attach_back(id, publisher);
    }
*/
    
    /**
     * <p>指定されたPublisherを、データ更新通知先リストの先頭に追加します。</p>
     * 
     * @param id 指定されたPublisherに割り当てるID
     * @param publisher 登録するPublisherオブジェクト
     */
/*
    public void attach_front(final String id, PublisherBase publisher) {
        this.m_publishers.add(0, new Publisher(id, publisher));
    }
*/
    
    /**
     * <p>指定されたPublisherを、データ更新通知先リストの最後尾に追加します。</p>
     * 
     * @param id 指定されたPublisherに割り当てるID
     * @param publisher 登録するPublisherオブジェクト
     */
/*
    public void attach_back(final String id, PublisherBase publisher) {
        this.m_publishers.add(new Publisher(id, publisher));
    }
*/
    
    /**
     * <p>指定されたPublisherを、データ更新先通知先から削除します。</p>
     * 
     * @param id 削除するPublisherに割り当てたID
     * @return 正常にデータ更新先通知先から削除できた場合は、そのPublisherオブジェクトを返します。<br />
     * 指定したIDに対応するPublisherオブジェクトが存在しない場合には、nullを返します。
     */
/*
    public PublisherBase detach(final String id) {
        
        for (Iterator<Publisher> it = this.m_publishers.iterator(); it.hasNext(); ) {
            Publisher publisher = it.next();
            if (publisher.id.equals(id)) {
                it.remove();
                return publisher.publisher;
            }
        }
        
        return null;
    }
*/
    
    /**
     * <p>登録されているすべてのPublisherオブジェクトに、データ更新を通知します。</p>
     */
/*
    public void update() { // notifyメソッドはObjectクラスで使用されているので、メソッド名を変更した。
        try {
            for (Iterator<Publisher> it = this.m_publishers.iterator(); it.hasNext(); ) {
                Publisher publisher = it.next();
                publisher.publisher.update();
            }
        } catch(Exception ex) {
        }
    }
*/
    
    /**
     * <p>ポート名です。</p>
     */
    protected String m_name = new String();

    protected class Publisher {
        
        public Publisher(final String _id, PublisherBase _publisher) {
            
            this.id = _id;
            this.publisher = _publisher;
        }
        
        public String id;
        public PublisherBase publisher;
    }
    
    /**
     * <p> Publish interface information </p>
     *
     * <p>This operation is pure virutal function that would be called at the
     * beginning of the notify_connect() process sequence.
     * In the notify_connect(), the following methods would be called in order.</p>
     * 
     * <p> - publishInterfaces() </p>
     * <p> - connectNext() </p>
     * <p> - subscribeInterfaces() </p>
     * <p> - updateConnectorProfile()  </p>
     *
     * <p> This operation should create the new connection for the new
     * connector_id, and should update the connection for the existing
     * connection_id. </p>
     *
     * @param cprof The connection profile information
     *
     * @return The return code of ReturnCode_t type.
     */
    protected ReturnCode_t 
    publishInterfaces(ConnectorProfileHolder cprof) {
        rtcout.println(rtcout.TRACE, "publishInterfaces()");

        // prop: [port.outport].
        Properties prop = m_properties;
        {
            Properties conn_prop = new Properties();
            NVListHolder nvlist = new NVListHolder(cprof.value.properties);
            NVUtil.copyToProperties(conn_prop, nvlist);
            prop.merge(conn_prop.getNode("dataport")); //merge ConnectorProfile
        }


        /*
         * Because properties of ConnectorProfileHolder was merged, 
         * the accesses such as prop["dataflow_type"] and 
         * prop["interface_type"] become possible here.
         */
        String dflow_type = prop.getProperty("dataflow_type");
        StringUtil.normalize(dflow_type);


        if (dflow_type.equals("push")) {
            rtcout.println(rtcout.DEBUG, 
                           "dataflow_type = push .... do nothing");
            return ReturnCode_t.RTC_OK;
        }
        else if (dflow_type.equals("pull")) {
            rtcout.println(rtcout.DEBUG, 
                           "dataflow_type = pull .... create PushConnector");
            OutPortProvider provider=createProvider(cprof, prop);
            if (provider == null) {
                return ReturnCode_t.BAD_PARAMETER;
            }

            // create InPortPushConnector
            OutPortConnector connector=createConnector(cprof, prop, provider);
            if (connector == null) {
                return ReturnCode_t.RTC_ERROR;
            }

            rtcout.println(rtcout.DEBUG, 
                           "publishInterface() successfully finished.");
            return ReturnCode_t.RTC_OK;
        }

        rtcout.println(rtcout.ERROR, "unsupported dataflow_type");

        return ReturnCode_t.BAD_PARAMETER;
    }
    /**
     * <p> Subscribe to the interface </p>
     *
     * <p> This operation is pure virutal function that would be called at the
     * middle of the notify_connect() process sequence.
     * In the notify_connect(), the following methods would be called in order. </p>
     *
     * <p> - publishInterfaces() </p>
     * <p> - connectNext() </p>
     * <p> - subscribeInterfaces() </p>
     * <p> - updateConnectorProfile() </p>
     *
     * @param cprof The connection profile information
     *
     * @return The return code of ReturnCode_t type.
     */
    protected ReturnCode_t subscribeInterfaces(
            final ConnectorProfileHolder cprof) {
        rtcout.println(rtcout.TRACE, "subscribeInterfaces()");
        // prop: [port.outport].
        Properties prop = m_properties;
        {
            Properties conn_prop = new Properties();
            NVListHolder nvlist = new NVListHolder(cprof.value.properties);
            NVUtil.copyToProperties(conn_prop, nvlist);
            prop.merge(conn_prop.getNode("dataport")); //merge ConnectorProfile
        }

        /*
         * Because properties of ConnectorProfileHolder was merged, 
         * the accesses such as prop["dataflow_type"] and 
         * prop["interface_type"] become possible here.
         */
        String dflow_type = prop.getProperty("dataflow_type");
        StringUtil.normalize(dflow_type);
        if (dflow_type.equals("push")) {
            rtcout.println(rtcout.DEBUG, 
                           "dataflow_type = push .... create PushConnector");

            //interface 
            InPortConsumer consumer = createConsumer(cprof, prop);
            if (consumer == null) {
                return ReturnCode_t.BAD_PARAMETER;
            }

            // create OutPortPushConnector
            OutPortConnector connector = createConnector(cprof, prop, consumer);
            if (connector == null) {
                return ReturnCode_t.RTC_ERROR;
            }

            rtcout.println(rtcout.DEBUG, 
                           "publishInterface() successfully finished.");
            return ReturnCode_t.RTC_OK;
        }
        else if (dflow_type.equals("pull")) {
            rtcout.println(rtcout.DEBUG, 
                           "dataflow_type = pull .... do nothing.");
            return ReturnCode_t.RTC_OK;
        }

        rtcout.println(rtcout.ERROR, "unsupported dataflow_type.");
        return ReturnCode_t.BAD_PARAMETER;

    }
    /*
     * <p> Disconnect the interface connection </p>
     *
     * <p>This operation is pure virutal function that would be called at the
     * end of the notify_disconnect() process sequence.
     * In the notify_disconnect(), the following methods would be called. </p>
     * <p> - disconnectNext() </p>
     * <p> - unsubscribeInterfaces() </p> 
     * <p> - eraseConnectorProfile() </p> 
     *
     * @param connector_profile The profile information associated with 
     *                          the connection
     *
     */
    protected void 
    unsubscribeInterfaces(final ConnectorProfile connector_profile) {
        rtcout.println(rtcout.TRACE, "unsubscribeInterfaces()");

        String id = connector_profile.connector_id;
        rtcout.println(rtcout.PARANOID, "connector_id: " + id);

        Iterator it = m_connectors.iterator();
        while (it.hasNext()) {
            InPortConnector connector = (InPortConnector)it.next();
            if (id.equals(connector.id())) {
                // Connector's dtor must call disconnect()
                it.remove();
                rtcout.println(rtcout.TRACE, "delete connector: " + id);
                return;
            }
        }
        rtcout.println(rtcout.ERROR, "specified connector not found: " + id);
        return;
   }
  /**
   * <p> Activate all Port interfaces </p>
   * <p> This operation activate all interfaces that is registered in the
   * ports.</p>
   */
  public void activateInterfaces() {
      for (int i=0, len=m_connectors.size(); i < len; ++i) {
          m_connectors.elementAt(i).activate();
      }
  }
  
  /**
   * <p> Deactivate all Port interfaces </p>
   * <p> This operation deactivate all interfaces that is registered in the
   * ports. </p>
   */
  public void deactivateInterfaces() {
      for (int i=0, len=m_connectors.size(); i < len; ++i) {
          m_connectors.elementAt(i).deactivate();
      }
  }
    /**
     * <p>データ更新通知先として登録されているPublisherオブジェクトのリストです。</p>
     */
    /**
     * <p> Configureing outport </p>
     *
     * <p> This operation configures the outport based on the properties. </p>
     *
     */
    protected void configure(){
    }
    /**
     * <p> OutPort provider initialization </p>
     */
    protected void initProviders(){
        rtcout.println(rtcout.TRACE, "initProviders()");

        // create OutPort providers
        OutPortProviderFactory<OutPortProvider,String> factory 
            = OutPortProviderFactory.instance();
        Set provider_types = factory.getIdentifiers();
        rtcout.println(rtcout.DEBUG, 
                       "available providers: " + provider_types.toString());

//#ifndef RTC_NO_DATAPORTIF_ACTIVATION_OPTION
        String string_normalize = StringUtil.normalize(m_properties.getProperty("provider_types"));
        if (m_properties.hasKey("provider_types")!=null &&
           !string_normalize.equals("all")) {
            rtcout.println(rtcout.DEBUG, 
                       "allowed providers: " 
                       + m_properties.getProperty("provider_types"));

            Set temp_types = provider_types;
            provider_types.clear();
            Vector<String> active_types 
                = StringUtil.split(m_properties.getProperty("provider_types"), 
                                   ",");

            Set temp_types_set = new HashSet(temp_types);
            Set active_types_set = new HashSet(active_types);
            Iterator it = temp_types_set.iterator();
            while(it.hasNext()) {
                String str = (String)it.next();
                if(active_types_set.contains(str)) {
                    provider_types.add(str);
                }
            }
      }
//#endif
    
        // OutPortProvider supports "pull" dataflow type
        if (provider_types.size() > 0) {
            rtcout.println(rtcout.DEBUG, 
                           "dataflow_type pull is supported");
            appendProperty("dataport.dataflow_type", "push");
            appendProperty("dataport.interface_type",
                           provider_types.toString());
        }

        m_providerTypes = (Vector<String>)provider_types;
    }

    /**
     * <p> InPort consumer initialization </p>
     */
    protected void initConsumers() {
        rtcout.println(rtcout.TRACE, "initConsumers()");

        // create InPort consumers
        InPortProviderFactory<InPortProvider,String> factory 
            = InPortProviderFactory.instance();
        Set consumer_types = factory.getIdentifiers();
        rtcout.println(rtcout.DEBUG, 
                       "available InPortConsumer: "+consumer_types.toString());

//#ifndef RTC_NO_DATAPORTIF_ACTIVATION_OPTION
        String string_normalize = StringUtil.normalize(m_properties.getProperty("consumer_types"));
        if (m_properties.hasKey("consumer_types")!=null &&
            !string_normalize.equals("all")) {
            rtcout.println(rtcout.DEBUG, 
                       "allowed consumers: " 
                       + m_properties.getProperty("consumer_types"));

            Set temp_types = consumer_types;
            consumer_types.clear();
            Vector<String> active_types 
                = StringUtil.split(m_properties.getProperty("consumer_types"), 
                                   ",");

            Set temp_types_set = new HashSet(temp_types);
            Set active_types_set = new HashSet(active_types);
            Iterator it = temp_types_set.iterator();
            while(it.hasNext()) {
                String str = (String)it.next();
                if(active_types_set.contains(str)) {
                    consumer_types.add(str);
                }
            }
        }
//#endif

        // OutPortConsumer supports "push" dataflow type
        if (consumer_types.size() > 0) {
            rtcout.println(rtcout.DEBUG, 
                           "dataflow_type pull is supported");
            appendProperty("dataport.dataflow_type", "puish");
            appendProperty("dataport.interface_type",
                           consumer_types.toString());
        }

        m_consumerTypes = (Vector<String>)consumer_types;

    }
    /**
     * <p> OutPort provider creation </p>
     */
    protected OutPortProvider 
    createProvider(ConnectorProfileHolder cprof,
                                    Properties prop) {
        if (prop.getProperty("interface_type").length()!=0 &&
            !StringUtil.includes(m_providerTypes, 
                      prop.getProperty("interface_type"),
                      true)) {
            rtcout.println(rtcout.ERROR, "no provider found");
            rtcout.println(rtcout.ERROR, 
                       "interface_type:  "+prop.getProperty("interface_type"));
            rtcout.println(rtcout.ERROR, 
                       "interface_types:  "+m_providerTypes.toString());
            return null;
        }
    
        rtcout.println(rtcout.DEBUG, 
                       "interface_type:  "+prop.getProperty("interface_type"));
        OutPortProvider provider;
        OutPortProviderFactory<OutPortProvider,String> factory 
            = OutPortProviderFactory.instance();
        provider = factory.createObject(prop.getProperty("interface_type"));
    
        if (provider != null) {
            rtcout.println(rtcout.DEBUG, "provider created");
            provider.init(prop.getNode("provider"));

            NVListHolder nvlist = new NVListHolder(cprof.value.properties);
            if (!provider.publishInterface(nvlist)) {
                rtcout.println(rtcout.ERROR, 
                               "publishing interface information error");
                factory.deleteObject(provider);
                return null;
            }
            return provider;
        }

        rtcout.println(rtcout.ERROR, "provider creation failed");
        return null;
    
    }
    /**
     * <p> InPort consumer creation </p>
     */
    protected InPortConsumer 
    createConsumer(final ConnectorProfileHolder cprof,
                                   Properties prop) {
        if (prop.getProperty("interface_type").length()!=0 &&
            !StringUtil.includes(m_consumerTypes, 
                                 prop.getProperty("interface_type"),
                                 true)) {
            rtcout.println(rtcout.ERROR, "no consumer found");
            rtcout.println(rtcout.ERROR, 
                       "interface_type:  "+prop.getProperty("interface_type"));
            rtcout.println(rtcout.ERROR, 
                       "interface_types:  "+m_consumerTypes.toString());
            return null;
        }
    
        rtcout.println(rtcout.DEBUG, 
                       "interface_type:  "+prop.getProperty("interface_type"));
        InPortConsumer consumer;
        InPortConsumerFactory<InPortConsumer,String> factory 
            = InPortConsumerFactory.instance();
        consumer = factory.createObject(prop.getProperty("interface_type"));
    
        if (consumer != null) {
            rtcout.println(rtcout.DEBUG, "consumer created");
            consumer.init(prop.getNode("consumer"));
    
            NVListHolder nvlist = new NVListHolder(cprof.value.properties);
            if (!consumer.subscribeInterface(nvlist)) {
                rtcout.println(rtcout.ERROR, 
                               "interface subscription failed.");
                factory.deleteObject(consumer);
                return null;
              }

            return consumer;
        }

        rtcout.println(rtcout.ERROR, "consumer creation failed");
        return null;
    
    }
    
    /**
     * <p> OutPortPushConnector creation </p>
     */
    protected OutPortConnector 
    createConnector(final ConnectorProfileHolder cprof,
                                      Properties prop,
                                      InPortConsumer consumer) {
        ConnectorBase.Profile profile 
            = new ConnectorBase.Profile( cprof.value.name,
                                  cprof.value.connector_id,
                                  CORBA_SeqUtil.refToVstring(cprof.value.ports),
                                  prop); 
        OutPortConnector connector = null;
        try {
            BufferBase<OutputStream> buffer = null;
            connector = new OutPortPushConnector(profile, consumer, buffer);

            if (connector == null) {
                rtcout.println(rtcout.ERROR, "old compiler? new returned 0;");
                return null;
            }
            rtcout.println(rtcout.TRACE, "OutPortPushConnector create");

            m_connectors.add(connector);
            rtcout.println(rtcout.PARANOID, 
                           "connector push backed: "+m_connectors.size());
            return connector;
        }
        catch (Exception e) {
            rtcout.println(rtcout.ERROR,"OutPortPullConnector creation failed");
            return null;
        }
    }
    /**
     * <p> OutPortPullConnector creation </p>
     */
    protected OutPortConnector 
    createConnector(final ConnectorProfileHolder cprof,
                                      Properties prop,
                                      OutPortProvider provider) {
        ConnectorBase.Profile profile 
            = new ConnectorBase.Profile(cprof.value.name,
                                 cprof.value.connector_id,
                                 CORBA_SeqUtil.refToVstring(cprof.value.ports),
                                 prop); 
        OutPortConnector connector = null;
        try {
            BufferBase<OutputStream> buffer = null;
            connector = new OutPortPullConnector(profile, provider, buffer);

            if (connector == null) {
                rtcout.println(rtcout.ERROR, "old compiler? new returned 0;");
                return null;
            }
            rtcout.println(rtcout.TRACE, "OutPortPushConnector create");

            m_connectors.add(connector);
            rtcout.println(rtcout.PARANOID, 
                           "connector push backed: "+m_connectors.size());
            return connector;
        }
        catch (Exception e) {
            rtcout.println(rtcout.ERROR,"OutPortPullConnector creation failed");
            return null;
        }
    }
    protected List<Publisher> m_publishers = new ArrayList<Publisher>();
    protected Properties m_properties = new Properties();
    protected Vector<OutPortConnector> m_connectors = new Vector<OutPortConnector>();
    protected Vector<InPortConsumer> m_consumers = new Vector<InPortConsumer>();
    protected Vector<String> m_providerTypes = new Vector<String>();
    protected Vector<String> m_consumerTypes = new Vector<String>();
    protected Vector<OutPortProvider> m_providers = new Vector<OutPortProvider>();
    
}

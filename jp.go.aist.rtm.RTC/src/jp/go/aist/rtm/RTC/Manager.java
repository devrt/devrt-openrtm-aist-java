package jp.go.aist.rtm.RTC;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.FileHandler;

import jp.go.aist.rtm.RTC.executionContext.ECFactoryBase;
import jp.go.aist.rtm.RTC.executionContext.ECFactoryJava;
import jp.go.aist.rtm.RTC.executionContext.ExecutionContextBase;
import jp.go.aist.rtm.RTC.executionContext.ExtTrigExecutionContext;
import jp.go.aist.rtm.RTC.executionContext.PeriodicExecutionContext;
import jp.go.aist.rtm.RTC.log.LogStream;
import jp.go.aist.rtm.RTC.log.Logbuf;
import jp.go.aist.rtm.RTC.log.LogbufOn;
import jp.go.aist.rtm.RTC.log.MedLogbuf;
import jp.go.aist.rtm.RTC.util.ORBUtil;
import jp.go.aist.rtm.RTC.util.Properties;
import jp.go.aist.rtm.RTC.util.RTCUtil;
import jp.go.aist.rtm.RTC.util.StringUtil;
import jp.go.aist.rtm.RTC.util.TimeValue;
import jp.go.aist.rtm.RTC.util.Timer;
import jp.go.aist.rtm.RTC.util.equalFunctor;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import RTC.RTObject;
import RTC.ReturnCode_t;

/**
* <p>各コンポーネントの管理を行うクラスです。</p>
*/
public class Manager {

    /**
     * <p>コンストラクタです。</p>
     */
    protected Manager() {
        
        m_initProc = null;
        m_Logbuf = new Logbuf();
        m_MedLogbuf = new MedLogbuf(m_Logbuf);
        rtcout = new LogStream(m_MedLogbuf);
        m_runner = null;
        m_terminator = null;
    }

    /**
     * <p>コピーコンストラクタです。</p>
     * 
     * @param rhs コピー元のManagerオブジェクト
     */
    public Manager(final Manager rhs) {
        
        m_initProc = null;
        m_Logbuf = new Logbuf();
        m_MedLogbuf = new MedLogbuf(m_Logbuf);
        rtcout = new LogStream(m_MedLogbuf);
        m_runner = null;
        m_terminator = null;
    }
    
    /**
     * <p>初期化を行います。Managerを使用する場合には、必ず本メソッドを呼ぶ必要があります。<br />
     * コマンドライン引数を与えて初期化を行います。Managerオブジェクトを取得する方法としては、
     * init(), instance()の2メソッドがありますが、初期化はinit()でのみ行われるため、
     * Managerオブジェクトの生存期間の最初にinit()メソッドを呼び出す必要があります。</p>
     * 
     * @param argv コマンドライン引数の配列
     */
    public static Manager init(String[] argv) {
        
        if (manager == null) {
            synchronized (manager_mutex) {
                if (manager == null) {
                    try {
                        manager = new Manager();
                        manager.initManager(argv);
                        manager.initLogger();
                        manager.initORB();
                        manager.initNaming();
                        manager.initExecContext();
                        manager.initTimer();
                        
                    } catch (Exception e) {
                        manager = null;
                    }
                }
            }
        }

        return manager;
    }
    
    /**
     * <p>Managerオブジェクトを取得します。
     * 本メソッドの呼び出しに先立っては、必ずinit()が呼び出し済みでなければなりません。</p>
     * 
     * @return Managerオブジェクト
     */
    public static Manager instance() {
        
        if (manager == null) {
            synchronized (manager_mutex) {
                if (manager == null) {
                    try {
                        manager = new Manager();
                        manager.initManager(null);
                        manager.initLogger();
                        manager.initORB();
                        manager.initNaming();
                        manager.initExecContext();
                        manager.initTimer();
                        
                    } catch (Exception e) {
                        manager = null;
                    }
                }
            }
        }

        return manager;
    }
    
    /**
     * <p>Managerの終了処理を行います。</p>
     */
    public void terminate() {
        
        if (m_terminator != null) {
            m_terminator.terminate();
        }
    }
    
    /**
     * <p>Managerオブジェクトを終了します。
     * ORBの終了後，同期を取って終了します。</p>
     */
    public void shutdown() {
        
        rtcout.println(rtcout.TRACE, "Manager.shutdown()");
        
        shutdownComponents();
        shutdownNaming();
        shutdownORB();
        shutdownManager();
        
        // 終了待ち合わせ
        if (m_runner != null) {
            try {
                m_runner.wait();
                
            } catch (InterruptedException e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught InterruptedException in Manager.shutdown().");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        } else {
            join();
        }
        
        shutdownLogger();
    }
    
    /**
     * <p>Manager終了処理の待ち合わせを行います。</p>
     */
    public void join() {
        
        rtcout.println(rtcout.TRACE, "Manager.join()");
        
        synchronized (Integer.valueOf(m_terminate_waiting)) {
            ++m_terminate_waiting;
        }
        
        while (true) {
            synchronized (Integer.valueOf(m_terminate_waiting)) {
                if (m_terminate_waiting > 1) {
                    break;
                }
            }
            
            try {
                Thread.sleep(0, 100 * 1000);
                
            } catch (InterruptedException e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught InterruptedException in Manager.join().");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>ログバッファを取得します。</p>
     *
　   * @return ログバッファ
     */ 
    public Logbuf getLogbuf() {
        return m_Logbuf;
    }

    /**
     * <p>マネージャのコンフィギュレーションを取得します。</p>
     *
　   * @return マネージャコンフィギュレーション
     */ 
    public Properties getConfig() {
        return m_config;
    }
    
    /**
     * <p>初期化プロシジャコールバックインタフェースを設定します。
     * マネージャが初期化されてアクティブ化された後に、
     * 設定されたコールバックインタフェースが呼び出されます。</p>
     * 
     * @param initProc コールバックインタフェース
     */
    public void setModuleInitProc(ModuleInitProc initProc) {
        m_initProc = initProc;
    }
    
    /**
     * <p>Managerをアクティブ化します。
     * 初期化後にrunManager()呼び出しに先立ってこのメソッドを呼び出す必要があります。</p>
     * 
     * <p>具体的には以下の処理が行われます。
     * <ol>
     * <li>CORBA POAManagerのアクティブ化</li>
     * <li>Manager CORBAオブジェクトのアクティブ化</li>
     * <li>ManagerへのCORBAオブジェクト参照の登録</li>
     * </ol>
     * </p>
     * 
     * @return 正常にアクティブ化できた場合はtrueを、さもなくばfalseを返します。
     */
    public boolean activateManager() {
        
        rtcout.println(rtcout.TRACE, "Manager.activateManager()");
        
        try {
            this.getPOAManager().activate();

            if (m_initProc != null) {
                m_initProc.myModuleInit(this);
            }
            
        } catch (Exception e) {
            rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.activateManager().");
            rtcout.println(rtcout.NORMAL, e.getMessage());
            return false;
        }
        
        return true;
    }

    /**
     * <p>Managerのメインループを実行します。本メソッドは、runManager(false)の呼び出しと同等です。</p>
     */
    public void runManager() {
        this.runManager(false);
    }
    
    /**
     * <p>Managerのメインループを実行します。
     * このメインループ内では、CORBA ORBのイベントループなどが処理されます。<br />
     * ブロッキングモードで起動された場合は、Manager#destroy()メソッドが呼び出されるまで、
     * 本runManager()メソッドは処理を戻しません。<br />
     * 非ブロッキングモードで起動された場合は、内部でイベントループを別スレッドで開始後、
     * ブロックせずに処理を戻します。</p>
     * 
     * @param noBlocking 非ブロッキングモードの場合はtrue、ブロッキングモードの場合はfalse
     */
    public void runManager(boolean noBlocking) {
        
        if (noBlocking) {
            rtcout.println(rtcout.TRACE, "Manager.runManager(): non-blocking mode");
            
            m_runner = new OrbRunner(m_pORB);
            m_runner.open("");
            
        } else {
            rtcout.println(rtcout.TRACE, "Manager.runManager(): blocking mode");
            
            m_pORB.run();

            rtcout.println(rtcout.TRACE, "Manager.runManager(): ORB was terminated");
            
            join();
        }
    }
    
    /**
     * <p>コンポーネントのモジュールをロードして、初期化メソッドを実行します。</p>
     *
     * @param moduleFileName モジュールファイル名
     * @param initFunc 初期化メソッド名
     */
    public void load(final String moduleFileName, final String initFunc) {
        
        rtcout.println(rtcout.TRACE, "Manager.load()");
        
        try {
            m_module.load(moduleFileName, initFunc);
            
        } catch (Exception e) {
            rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.load().");
            rtcout.println(rtcout.NORMAL, e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * <p>モジュールをアンロードします。</p>
     *
     * @param moduleFileName モジュールファイル名
     */ 
    public void unload(final String moduleFileName) {
        
        rtcout.println(rtcout.TRACE, "Manager.unload()");
        
        m_module.unload(moduleFileName);
    }
    
    /**
     * <p>すべてのモジュールをアンロードします。</p>
     */ 
    public void unloadAll() {
        
        rtcout.println(rtcout.TRACE, "Manager.unloadAll()");
        
        m_module.unloadAll();
    }
    
    /**
     * <p>ロード済みのモジュール名リストを取得します。</p>
     * 
　   * @return ロード済みモジュール名リスト
     */
    public Vector<String> getLoadedModules() {
        
        rtcout.println(rtcout.TRACE, "Manager.getLoadedModules()");
        
        return m_module.getLoadedModules();
    }
    
    /**
     * <p>ロード可能なモジュール名リストを取得します。</p>
     *
　   * @return ロード可能モジュール名リスト
     */
    public Vector<String> getLoadableModules() {
        
        rtcout.println(rtcout.TRACE, "Manager.getLoadableModules()");
        
        return m_module.getLoadableModules();
    }
    
    /**
     * <p>RTコンポーネントファクトリを登録します。</p>
     *
     * @param profile コンポーネントプロファイル 
     * @param new_func コンポーネント生成オブジェクト 
     * @param delete_func コンポーネント削除オブジェクト 
     *
　   * @return 登録に成功した場合はtrueを、さもなくばfalseを返します。
     */
    public boolean registerFactory(Properties profile, RtcNewFunc new_func,
            RtcDeleteFunc delete_func) {
        
        rtcout.println(rtcout.TRACE, "Manager.registerFactory("
                + profile.getProperty("type_name") + ")");

        try {
            FactoryBase factory = new FactoryJava(profile, new_func, delete_func);
            m_factory.registerObject(factory, new FactoryPredicate(factory));
            return true;
            
        } catch (Exception ex) {
            rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.registerFactory().");
            rtcout.println(rtcout.NORMAL, ex.getMessage());
            return false;
        }
    }

   /**
    * <p>ExecutionContextファクトリを登録します。</p>
    *
    * @param name ExecutionContext名称 
    * @return 登録に成功した場合はtrueを、さもなくばfalseを返します。
    */
    public boolean registerECFactory(final String name) {
        
        rtcout.println(rtcout.TRACE, "Manager.registerECFactory(" + name + ")");
        
        try {
            ECFactoryBase factory = new ECFactoryJava(name);
            m_ecfactory.registerObject(factory, new ECFactoryPredicate(factory));
            return true;
            
        } catch (Exception ex) {
            rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.registerECFactory().");
            rtcout.println(rtcout.NORMAL, ex.getMessage());
            return false;
        }
    }

    /**
     * <p>すべてのRTコンポーネントファクトリのリストを取得します。</p>
     * 
     * @return すべてのRTコンポーネントファクトリのリスト
     */
    public Vector<String> getModulesFactories() {
        
        rtcout.println(rtcout.TRACE, "Manager.getModulesFactories()");

        Vector<String> factoryIds = new Vector<String>();
        for (int i = 0; i < m_factory.m_objects.size(); i++) {
            factoryIds.add(m_factory.m_objects.elementAt(i).profile().getProperty("implementation_id"));
        }
        
        return factoryIds;
    }
    
    /**
     * <p>RTコンポーネントファクトリをクリアする。</p>
     */
    public void clearModulesFactories() {
        m_factory = new ObjectManager<String, FactoryBase>();
    }

    /**
     * <p>RTコンポーネントマネージャをクリアする。</p>
     */
    public void clearModules() {
        m_compManager = new ObjectManager<String, RTObject_impl>();
    }
    
    /**
     * <p>RTコンポーネントを生成します。</p>
     * 
     * @param moduleName モジュール名
     * @return 生成されたRTコンポーネントオブジェクト
     */
    public RTObject_impl createComponent(final String moduleName) {
        
        rtcout.println(rtcout.TRACE, "Manager.createComponent(" + moduleName + ")");

        // Create Component
        RTObject_impl comp = null;
        for (int i = 0; i < m_factory.m_objects.size(); i++) {
            FactoryBase factory = m_factory.m_objects.elementAt(i);
            
            if (factory.m_Profile.getProperty("implementation_id").equals(moduleName)) {
                comp = m_factory.m_objects.elementAt(i).create(this);
                if (comp == null) {
                    return null;
                }
                
                try {
                    m_objManager.activate(comp);
                    
                } catch (ServantAlreadyActive e) {
                    rtcout.println(rtcout.NORMAL, "Exception: Caught ServantAlreadyActive Exception in Manager.createComponent().");
                    rtcout.println(rtcout.NORMAL, e.getMessage());
                    e.printStackTrace();
                    
                } catch (WrongPolicy e) {
                    rtcout.println(rtcout.NORMAL, "Exception: Caught WrongPolicy Exception in Manager.createComponent().");
                    rtcout.println(rtcout.NORMAL, e.getMessage());
                    e.printStackTrace();
                    
                } catch (ObjectNotActive e) {
                    rtcout.println(rtcout.NORMAL, "Exception: Caught ObjectNotActive Exception in Manager.createComponent().");
                    rtcout.println(rtcout.NORMAL, e.getMessage());
                    e.printStackTrace();
                }
                
                rtcout.println(rtcout.TRACE, "RTC Created: " + moduleName);
            }
        }
        
        // Load configuration file specified in "rtc.conf"
        //
        // rtc.conf:
        // [category].[type_name].config_file = file_name
        // [category].[instance_name].config_file = file_name
        configureComponent(comp);
        //------------------------------------------------------------
        // Component initialization
        if( comp.initialize() != ReturnCode_t.RTC_OK ) {
            rtcout.println(rtcout.TRACE, "RTC initialization failed: " + moduleName);
            comp.exit();
            return null;
        }
        rtcout.println(rtcout.TRACE, "RTC initialization succeeded: " + moduleName);
        
        // Component initialization
        bindExecutionContext(comp);

        // Bind component to naming service
        registerComponent(comp);
        
        return comp;
    }
    
    /**
     * <p>指定したRTコンポーネントを登録解除します。</p>
     * 
     * @param comp 登録解除するRTコンポーネントオブジェクト
     */
    public void cleanupComponent(RTObject_impl comp) {
        
        rtcout.println(rtcout.TRACE, "Manager.shutdownComponents()");
        
        unregisterComponent(comp);
    }
    
    /**
     * <p>RTコンポーネントを、直接にManagerに登録します。</p>
     *
     * @param comp 登録対象のRTコンポーネントオブジェクト 
     * @return 正常に登録できた場合はtrueを、さもなくばfalseを返します。
     */
    public boolean registerComponent(RTObject_impl comp) {
        
        rtcout.println(rtcout.TRACE, "Manager.registerComponent("
                + comp.getInstanceName() + ")");
        
        // NamingManagerのみで代用可能
        m_compManager.registerObject(comp, new InstanceName(comp));
        
        String[] names = comp.getNamingNames();
        for (int i = 0; i < names.length; ++i) {
            rtcout.println(rtcout.TRACE, "Bind name: " + names[i]);
            
            m_namingManager.bindObject(names[i], comp);
        }

        return true;
    }
    
    /**
     * <p>指定したRTコンポーネントを登録解除します。</p>
     * 
     * @param comp 登録解除するRTコンポーネントオブジェクト
     */
    public boolean unregisterComponent(RTObject_impl comp) {
        
        rtcout.println(rtcout.TRACE, "Manager.unregisterComponent("
                + comp.getInstanceName() + ")");
        
        // NamingManager のみで代用可能
        m_compManager.unregisterObject(new InstanceName(comp));
      
        String[] names = comp.getNamingNames();
        for (int i = 0; i < names.length; ++i) {
            rtcout.println(rtcout.TRACE, "Unbind name: " + names[i]);
            
            m_namingManager.unbindObject(names[i]);
        }
        
        return true;
    }
    
    /**
     * <p>指定したRTコンポーネントに、ExecutionContextをバインドします。</p>
     * 
     * @param comp バインド対象のRTコンポーネントオブジェクト
     * @return 正常にバインドできた場合はtrueを、さもなくばfalseを返します。
     */
    public boolean bindExecutionContext(RTObject_impl comp) {
        
        rtcout.println(rtcout.TRACE, "Manager.bindExecutionContext()");
        rtcout.println(rtcout.TRACE, "ExecutionContext type: "
                + m_config.getProperty("exec_cxt.periodic.type"));

        RTObject rtobj = comp.getObjRef();

        ExecutionContextBase exec_cxt;

        if (RTCUtil.isDataFlowParticipant(rtobj)) {
            final String ectype = m_config.getProperty("exec_cxt.periodic.type");
            exec_cxt = ((ECFactoryBase) (m_ecfactory.find(new ECFactoryPredicate(ectype)))).create();
            
            try {
                m_objManager.activate(exec_cxt);
                
            } catch (ServantAlreadyActive e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught ServantAlreadyActive Exception in Manager.bindExecutionContext() DataFlowParticipant.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
                
            } catch (WrongPolicy e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught WrongPolicy Exception in Manager.bindExecutionContext() DataFlowParticipant.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
                
            } catch (ObjectNotActive e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught ObjectNotActive Exception in Manager.bindExecutionContext() DataFlowParticipant.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
            
            final String rate = m_config.getProperty("exec_cxt.periodic.rate");
            exec_cxt.set_rate(Double.valueOf(rate).doubleValue());
        }
        else {
            final String ectype = m_config.getProperty("exec_cxt.evdriven.type");
            exec_cxt = ((ECFactoryBase) (m_ecfactory.find(new ECFactoryPredicate(ectype)))).create();
            
            try {
                m_objManager.activate(exec_cxt);
                
            } catch (ServantAlreadyActive e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught ServantAlreadyActive Exception in Manager.bindExecutionContext() FsmParticipant.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
                
            } catch (WrongPolicy e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught WrongPolicy Exception in Manager.bindExecutionContext() FsmParticipant.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
                
            } catch (ObjectNotActive e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught ObjectNotActive Exception in Manager.bindExecutionContext() FsmParticipant.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        }

        exec_cxt.add(rtobj);
        exec_cxt.start();
        m_ecs.add(exec_cxt);
        
        return true;
    }
    
    /**
     * <p>Managerに登録されているRTコンポーネントを削除します。</p>
     * <p>※未実装</p>
     *
     * @param instanceName 削除対象のRTコンポーネント名 
     */
    public void deleteComponent(final String instanceName) {
        
        rtcout.println(rtcout.TRACE, "Manager.deleteComponent(" + instanceName + ")");
    }
    
    /**
     * <p>Managerに登録されているRTコンポーネントを取得します。</p>
     * <p>※未実装</p>
     *
     * @param instanceName 取得対象RTコンポーネント名 
     * @return 対象RTコンポーネントオブジェクト
     */
    public RTObject_impl getComponent(final String instanceName) {
        
        rtcout.println(rtcout.TRACE, "Manager.getComponent(" + instanceName + ")");
        return null;
    }
    
    /**
     * <p>Managerに登録されている全てのRTコンポーネントを取得します。</p>
     *
     * @return RTコンポーネントのリスト
     */
    public Vector<RTObject_impl> getComponents() {
        
        rtcout.println(rtcout.TRACE, "Manager.getComponents()");
        
        return m_compManager.getObjects();
    }
    
    /**
     * <p>ORBを取得します。</p>
     *
     * @return ORBオブジェクト
     */
    public ORB getORB() {
        
        rtcout.println(rtcout.TRACE, "Manager.getORB()");
        
        return m_pORB;
    }
    
    /**
     * <p>RootPOAを取得します。</p>
     *
     * @return RootPOAオブジェクト
     */
    public POA getPOA() {
        
        rtcout.println(rtcout.TRACE, "Manager.getPOA()");
        
        return m_pPOA;
    }

    /**
     * <p>POAマネージャを取得します。</p>
     *
     * @return POAマネージャ
     */
    public POAManager getPOAManager() {
        
        rtcout.println(rtcout.TRACE, "Manager.getPOAManager()");
        
        return m_pPOAManager;
    }
    
    /**
     * <p>Managerの内部初期化処理を行います。</p>
     *
     * @param argv コマンドライン引数
     */
    protected void initManager(String[] argv) throws Exception {
        
        ManagerConfig config = new ManagerConfig(argv);
        if (m_config == null) {
            m_config = new Properties();
        }
        
        config.configure(m_config);
        m_config.setProperty("logger.file_name",
                formatString(m_config.getProperty("logger.file_name"), m_config));

        m_module = new ModuleManager(m_config);
        m_terminator = new Terminator(this);

        synchronized (m_terminator) {
            m_terminate_waiting = 0;
        }

        if (StringUtil.toBool(m_config.getProperty("timer.enable"), "YES", "NO", true)) {
            TimeValue tm = new TimeValue(0, 100);
            String tick = new String(m_config.getProperty("timer.tick"));
            if (! (tick == null || tick.equals(""))) {
                tm.convert((Double.valueOf(tick)).doubleValue());
                m_timer = new Timer(tm);
                m_timer.start();
            }
        }
    }
    
    /**
     * <p>Managerの終了処理を実行します。</p>
     */
    protected void shutdownManager() {
        
        rtcout.println(rtcout.TRACE, "Manager.shutdownManager()");
    }
    
    /**
     * <p>System loggerを初期化します。</p>
     *
     * @return 正常に初期化できた場合はtrueを、さもなくばfalseを返します。
     */
    protected boolean initLogger() {
        
        rtcout.setLogLevel(rtcout.SILENT);
        
        if (StringUtil.toBool(m_config.getProperty("logger.enable"), "YES", "NO", true)) {
            
            m_Logbuf = new LogbufOn();
            
            String logfile = new String(m_config.getProperty("logger.file_name"));
            if (logfile == null || logfile.equals("")) {
                logfile = "./rtc.log";
            }
            
            // Open logfile
            try {
                m_Logbuf.open(new FileHandler(logfile));
                
            } catch (SecurityException e) {
                e.printStackTrace();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            m_MedLogbuf = new MedLogbuf(m_Logbuf);
            rtcout = new LogStream(m_MedLogbuf);

            // Set suffix for log entry haeader.
            m_MedLogbuf.setSuffix(m_config.getProperty("manager.name"));

            // Set date format for log entry header
            m_MedLogbuf.setDateFmt(m_config.getProperty("logger.date_format"));

            // Loglevel was set from configuration file.
            rtcout.setLogLevel(m_config.getProperty("logger.log_level"));

            // Log stream mutex locking mode
            rtcout.setLogLock(StringUtil.toBool(
                    m_config.getProperty("logger.stream_lock"), "enable", "disable", false));

            rtcout.println(rtcout.INFO, m_config.getProperty("openrtm.version"));
            rtcout.println(rtcout.INFO, "Copyright (C) 2003-2008");
            rtcout.println(rtcout.INFO, "  Noriaki Ando");
            rtcout.println(rtcout.INFO, "  Task-intelligence Research Group,");
            rtcout.println(rtcout.INFO, "  Intelligent Systems Research Institute, AIST");
            rtcout.println(rtcout.INFO, "Manager starting.");
            rtcout.println(rtcout.INFO, "Starting local logging.");
        }
        
        return true;
    }
    
    /**
     * <p>System Loggerの終了処理を行います。</p>
     */
    protected void shutdownLogger() {
        rtcout.println(rtcout.TRACE, "Manager.shutdownLogger()");
    }
    
    /**
     * <p>CORBA ORBの初期化処理を行います。</p>
     *
     * @return 正常に初期化できた場合はtrueを、さもなくばfalseを返します。
     */
    protected boolean initORB() {

        rtcout.println(rtcout.TRACE, "Manager.initORB()");
        
        // Initialize ORB
        try {
            String[] args = createORBOptions().split(" ");

            // ORB initialization
            m_pORB = ORBUtil.getOrb(args);

            // Get the RootPOA
            Object obj = m_pORB.resolve_initial_references("RootPOA");
            m_pPOA = POAHelper.narrow(obj);
            if (m_pPOA == null) {
                rtcout.println(rtcout.ERROR, "Could not resolve RootPOA.");
                return false;
            }
            
            // Get the POAManager
            m_pPOAManager = m_pPOA.the_POAManager();
            m_objManager = new CorbaObjectManager(m_pORB, m_pPOA);
            
        } catch (Exception ex) {
            rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.initORB().");
            rtcout.println(rtcout.NORMAL, ex.getMessage());
            return false;
        }
        
        return true;
    }
    
    /**
     * <p>ORBのコマンドラインオプションを生成します。</p>
     *
     * @return ORBコマンドラインオプション
     */
    protected String createORBOptions() {
        
        String opt = new String(m_config.getProperty("corba.args"));
        String corba = new String(m_config.getProperty("corba.id"));
        String endpoint = new String(m_config.getProperty("corba.endpoint"));

        if (! (endpoint == null || endpoint.length() == 0)) {
            if (! (opt == null || opt.length() == 0)) {
                opt += " ";
            }
            if (corba.equals("omniORB")) {
                opt = "-ORBendPoint giop:tcp:" + endpoint;
            }
            else if (corba.equals("TAO")) {
                opt = "-ORBEndPoint iiop://" + endpoint;
            }
            else if (corba.equals("MICO")) {
                opt = "-ORBIIOPAddr inet:" + endpoint;
            }
        }
        
        return opt;
    }

    /**
     * <p>ORBの終了処理を行います。</p>
     */
    protected void shutdownORB() {
        
        rtcout.println(rtcout.TRACE, "Manager.shutdownORB()");
        
        try {
            while (m_pORB.work_pending()) {
                rtcout.println(rtcout.PARANOID, "Pending work still exists.");
                
                if (m_pORB.work_pending()) {
                    m_pORB.perform_work();
                }
            }
        } catch (Exception e) {
            rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.shutdownORB().");
            rtcout.println(rtcout.NORMAL, e.getMessage());
            e.getStackTrace();
        }
        
        rtcout.println(rtcout.DEBUG, "No pending works of ORB. Shutting down POA and ORB.");

        if (m_pPOA != null) {
            try {
                if (m_pPOAManager != null) {
                    m_pPOAManager.deactivate(false, true);
                }
                
                rtcout.println(rtcout.DEBUG, "POA Manager was deactivated.");
                
                m_pPOA.destroy(false, true);
                m_pPOA = null;
                
                rtcout.println(rtcout.DEBUG, "POA was destroid.");
                
            } catch (SystemException ex) {
                rtcout.println(rtcout.ERROR, "Caught SystemException during root POA destruction");
                
            } catch (Exception ex) {
                rtcout.println(rtcout.ERROR, "Caught unknown exception during POA destruction.");
            }
        }

        if (m_pORB != null) {
            try {
                m_pORB.shutdown(true);
                
                rtcout.println(rtcout.DEBUG, "ORB was shutdown.");
                rtcout.println(rtcout.DEBUG, "ORB was destroied.");
                
                m_pORB = null;
                
            } catch (SystemException ex) {
                rtcout.println(rtcout.ERROR, "Caught SystemException during ORB shutdown");
                
            } catch (Exception ex) {
                rtcout.println(rtcout.ERROR, "Caught unknown exception during ORB shutdown.");
            }
        }
    }
    
    /**
     * <p>NamingManagerを初期化します。</p>
     */
    protected boolean initNaming() {
        
        rtcout.println(rtcout.TRACE, "Manager.initNaming()");
        
        m_namingManager = new NamingManager(this);

        // If NameService is disabled, return immediately
        if (! StringUtil.toBool(m_config.getProperty("naming.enable"), "YES", "NO", true)) {
            return true;
        }

        // NameServer registration for each method and servers
        String[] meth = m_config.getProperty("naming.type").split(",");

        for (int i = 0; i < meth.length; ++i) {
            String names[] = m_config.getProperty(meth[i] + ".nameservers").split(",");

            for (int j = 0; j < names.length; ++j) {
                rtcout.println(rtcout.TRACE, "Register Naming Server: " + meth[i] + " " + names[j]);
                
                String[] nameServer = names[j].split(":");
                if (nameServer.length == 1 && !nameServer[0].equals("")) {
                    names[j] += ":2809";
                }
                
                if (!names[j].equals("")) {
                    m_namingManager.registerNameServer(meth[i], names[j]);
                }
            }
        }

        // NamingManager Timer update initialization
        if (StringUtil.toBool(m_config.getProperty("naming.update.enable"), "YES", "NO", true)) {
            TimeValue tm = new TimeValue(10, 0); // default interval = 10sec
                                                  // for safty
            
            String intr = new String(m_config.getProperty("naming.update.interval"));
            if (! (intr == null || intr.equals(""))) {
                tm.convert(Double.valueOf(intr).doubleValue());
            }
            
            if (m_timer != null) {
                m_timer.registerListenerObj(m_namingManager, tm);
            }
        }
        
        return true;
    }
    
    /**
     * <p>NamingManagerの終了処理を行います。</p>
     */
    protected void shutdownNaming() {
        
        rtcout.println(rtcout.TRACE, "Manager.shutdownNaming()");
      
        m_namingManager.unbindAll();
    }
    
    /**
     * <p>ネーミングサービスに登録されているコンポーネントの終了処理を行います。</p>
     */
    protected void shutdownComponents() {
        
        rtcout.println(rtcout.TRACE, "Manager.shutdownComponents()");
        
        Vector<RTObject_impl> comps = m_namingManager.getObjects();
        for (int i = 0; i < comps.size(); ++i) {
            try {
                comps.elementAt(i).exit();
                Properties p = new Properties(comps.elementAt(i).getInstanceName());
                p.merge(comps.elementAt(i).getProperties());
                
                rtcout.level(LogStream.PARANOID);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        for (int i = 0; i < m_ecs.size(); ++i) {
            try {
                m_pPOA.deactivate_object(m_pPOA.servant_to_id(m_ecs.elementAt(i)));
                
            } catch (Exception e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.shutdownComponents().");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * <p>RTコンポーネントのコフィグレーション設定を行います。</p>
     * 
     * @param comp コンフィグレーション設定対象のRTコンポーネント
     */
    protected void configureComponent(RTObject_impl comp) {
        
        String category = new String(comp.getCategory());
        String type_name = new String(comp.getTypeName());
        String inst_name = new String(comp.getInstanceName());
      
        String type_conf = new String(category + "." + type_name + ".config_file");
        String name_conf = new String(category + "." + inst_name + ".config_file");
      
        Properties type_prop = new Properties();
        Properties name_prop = new Properties();
      
        // Load "category.instance_name.config_file"
        if (!(m_config.getProperty(name_conf) == null
                || m_config.getProperty(name_conf).length() == 0)) {
            
            try {
                BufferedReader conff = new BufferedReader(
                        new FileReader(m_config.getProperty(name_conf)));
                name_prop.load(conff);
                
            } catch (FileNotFoundException e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught FileNotFoundException in Manager.configureComponent() name_conf.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
                
            } catch (Exception e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught unknown in Manager.configureComponent() name_conf.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        }

        if (!(m_config.getProperty(type_conf) == null
                || m_config.getProperty(type_conf).length() == 0)) {
            
            try {
                BufferedReader conff = new BufferedReader(
                        new FileReader(m_config.getProperty(type_conf)));
                type_prop.load(conff);
                
            } catch (FileNotFoundException e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught FileNotFoundException in Manager.configureComponent() type_conf.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
                
            } catch (Exception e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.configureComponent() type_conf.");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        }

        // Merge Properties. type_prop is merged properties
        type_prop.merge(name_prop);
        comp.getProperties().merge(type_prop);

        // ------------------------------------------------------------
        // Format component's name for NameService
        StringBuffer naming_formats = new StringBuffer();
        Properties comp_prop = comp.getProperties();

        naming_formats.append(m_config.getProperty("naming.formats"));
        naming_formats.append(", " + comp_prop.getProperty("naming.formats"));
        String naming_formats_result = StringUtil.flatten(
                StringUtil.unique_sv(naming_formats.toString().split(",")));

        comp.getProperties().setProperty("naming.formats", naming_formats.toString());
        String naming_names = this.formatString(naming_formats_result, comp.getProperties());
        comp.getProperties().setProperty("naming.names", naming_names);
    }
    
    /**
     * <p>ExecutionContextを初期化します。</p>
     * 
     * @return 正常に初期化できた場合はtrueを、さもなくばfalseを返します。
     */
    protected boolean initExecContext() {
        
        rtcout.println(rtcout.TRACE, "Manager::initExecContext()");
        
        PeriodicExecutionContext.PeriodicExecutionContextInit(this);
        ExtTrigExecutionContext.ExtTrigExecutionContextInit(this);
        
        return true;
    }
    
    /**
     * <p>Timerを初期化します。</p>
     */
    protected boolean initTimer() {
        return true;
    }
    
    /**
     * <p>プロパティファイルを読み込んで、指定されたPropertiesオブジェクトに設定します。</p>
     * 
     * @param properties 設定対象のPropertiesオブジェクト
     * @param fileName プロパティファイル名
     * @return 正常に設定できた場合はtrueを、さもなくばfalseを返します。
     */
    protected boolean mergeProperty(Properties properties, final String fileName) {
        
        if (fileName == null) {
            rtcout.println(rtcout.ERROR, "Invalid configuration file name.");

            return false;
        }
        
        if (! (fileName.length() == 0)) {

            try {
                BufferedReader conff = new BufferedReader(new FileReader(fileName));
                properties.load(conff);
                conff.close();
                
                return true;

            } catch (FileNotFoundException e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught FileNotFoundException in Manager.mergeProperty().");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();

            } catch (Exception e) {
                rtcout.println(rtcout.NORMAL, "Exception: Caught unknown Exception in Manager.mergeProperty().");
                rtcout.println(rtcout.NORMAL, e.getMessage());
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * <p>指定されたPropertiesオブジェクトの内容を、指定された書式に従って文字列として出力します。</p>
     * 
     * @param namingFormat 書式指定
     * @param properties 出力対象となるPropertiesオブジェクト
     * @return Propertiesオブジェクトの内容を文字列出力したもの
     */
    protected String formatString(final String namingFormat, Properties properties) {
        
        StringBuffer str = new StringBuffer();
        int count = 0;

        for (int i = 0; i < namingFormat.length(); i++) {
            char c = namingFormat.charAt(i);
            if (c == '%') {
                ++count;
                if ((count % 2) == 0) {
                    str.append(c);
                }
            } else {
                if (count > 0 && (count % 2) != 0) {
                    count = 0;
                    if (c == 'n') {
                        str.append(properties.getProperty("instance_name"));
                    }
                    else if (c == 't') {
                        str.append(properties.getProperty("type_name"));
                    }
                    else if (c == 'm') {
                        str.append(properties.getProperty("type_name"));
                    }
                    else if (c == 'v') {
                        str.append(properties.getProperty("version"));
                    }
                    else if (c == 'V') {
                        str.append(properties.getProperty("vendor"));
                    }
                    else if (c == 'c') {
                        str.append(properties.getProperty("category"));
                    }
                    else if (c == 'h') {
                        str.append(m_config.getProperty("manager.os.hostname"));
                    }
                    else if (c == 'M') {
                        str.append(m_config.getProperty("manager.name"));
                    }
                    else if (c == 'p') {
                        str.append(m_config.getProperty("manager.pid"));
                    }
                    else {
                        str.append(c);
                    }
                } else {
                    count = 0;
                    str.append(c);
                }
            }
        }
        
        return str.toString();
    }
    
    /**
     * <p>唯一のManagerインスタンスです。</p>
     */
    protected static Manager manager;
    /**
     * <p>Manager用ミューテックス変数です。</p>
     */
    protected static String manager_mutex = new String();
    /**
     * <p>ORB</p>
     */
    protected ORB m_pORB;
    /**
     * <p>POA</p>
     */
    protected POA m_pPOA;
    /**
     * <p>POAManager</p>
     */
    protected POAManager m_pPOAManager;
    
    /**
     * <p>ユーザコンポーネント初期化プロシジャオブジェクト</p>
     */
    protected ModuleInitProc m_initProc;
    /**
     * <p>Managerコンフィギュレーション</p>
     */
    protected Properties m_config = new Properties();
    /**
     * <p>Module Manager</p>
     */
    protected ModuleManager m_module;
    /**
     * <p>Naming Manager</p>
     */
    protected NamingManager m_namingManager;
    /**
     * <p>CORBA Object Manager</p>
     */
    protected CorbaObjectManager m_objManager;
    /**
     * <p>Timer</p>
     */
    protected Timer m_timer;
    /**
     * <p>ロガーバッファ</p>
     */
    protected Logbuf m_Logbuf;
    /**
     * <p>ロガー仲介バッファ</p>
     */
    protected MedLogbuf m_MedLogbuf;
    /**
     * <p>ロガーストリーム</p>
     */
    protected LogStream rtcout;
    
    /**
     * <p>Object検索用ヘルパークラスです。</p>
     */
    protected class InstanceName implements equalFunctor {
        
        public InstanceName(RTObject_impl comp) {
            m_name = new String(comp.getInstanceName());
        }
        
        public InstanceName(final String name) {
            m_name = new String(name);
        }
        
        public boolean equalof(java.lang.Object comp) {
            return m_name.equals(((RTObject_impl)comp).getInstanceName());
        }
        
        public String m_name;
    }
    
    /**
     * <p>Component Manager</p>
     */
    protected ObjectManager<String, RTObject_impl> m_compManager = new ObjectManager<String, RTObject_impl>();
    
    /**
     * <p>Factory検索用ヘルパークラスです。</p>
     */
    protected class FactoryPredicate implements equalFunctor {
        
        public FactoryPredicate(final String name) {
            m_name = name;
        }
        
        public FactoryPredicate(FactoryBase factory) {
            m_name = factory.profile().getProperty("implementation_id");
        }
        
        public boolean equalof(java.lang.Object factory) {
            return m_name.equals(((FactoryBase)factory).profile().getProperty("implementation_id"));
        }
        
        public String m_name;
    }
    
    /**
     * <p>Component Factory Manager</p>
     */
    protected ObjectManager<String, FactoryBase> m_factory = new ObjectManager<String, FactoryBase>();
    
    /**
     * <p>ECFactory検索用ヘルパークラスです。</p>
     */
    class ECFactoryPredicate implements equalFunctor {
        
        public ECFactoryPredicate(final String name) {
            m_name = name;
        }
        
        public ECFactoryPredicate(ECFactoryBase factory) {
            m_name = factory.name();
        }
        
        public boolean equalof(java.lang.Object factory) {
            return m_name.equals(((ECFactoryBase)factory).name());
        }
        
        public String m_name;
    }
    
    /**
     * <p>ExecutionContext Factory</p>
     */
    protected ObjectManager<String, java.lang.Object> m_ecfactory = new ObjectManager<String, java.lang.Object>();
    /**
     * <p>ExecutionContext</p>
     */
    protected Vector<ExecutionContextBase> m_ecs = new Vector<ExecutionContextBase>();
    /**
     * <p>ORB実行用ヘルパークラスです。</p>
     */
    protected class OrbRunner implements Runnable {

        public OrbRunner(ORB orb) {
            m_pORB = orb;
            this.open("");
        }

        public int open(String args) {
            // activate();
            Thread t = new Thread(this);
            t.start();
            return 0;
        }

        public int svc() {
            m_pORB.run();
            Manager.instance().shutdown();
            return 0;
        }

        public int close(long flags) {
            return 0;
        }

        public void run() {
            this.svc();
        }

        private ORB m_pORB;
    }
    /**
     * <p>ORB Runner</p>
     */
    protected OrbRunner m_runner;
    
    /**
     * <p>終了処理用ヘルパークラスです。</p>
     */
    protected class Terminator implements Runnable {

        public Terminator(Manager manager) {
            m_manager = manager;
        }
        
        public void terminate() {
            this.open("");
        }
        
        public int open(String args) {
//            activate();
            Thread t = new Thread(this);
            t.start();
            return 0;
        }
        
        public int svc() {
            Manager.instance().shutdown();
            return 0;
        }
        
        public void run() {
            this.svc();
        }
        
        public Manager m_manager;
    }

    /**
     * <p>Terminator</p>
     */
    protected Terminator m_terminator;
    /**
     * <p>Terminator用カウンタ</p>
     */
    protected int m_terminate_waiting;
}

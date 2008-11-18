package jp.go.aist.rtm.RTC;

import jp.go.aist.rtm.RTC.util.Properties;
import RTC.ComponentProfile;
import RTC.ExecutionContextListHolder;
import RTC.ExecutionKind;
import RTC.LifeCycleState;
import RTC.PortListHolder;
import RTC.ReturnCode_t;
import RTMExamples.SeqIO.SeqIn;
import RTMExamples.SeqIO.SeqInComp;
import _SDOPackage.NVListHolder;

/**
* SeqIn　テスト(21)
* 対象クラス：SeqIn, SeqInComp
*/
public class SeqInTest extends SampleTest {
    private String configPath;
    private RTObject_impl comp;
    private  Manager manager;

    protected void setUp() throws Exception {
        super.setUp();
        java.io.File fileCurrent = new java.io.File(".");
        String rootPath = fileCurrent.getAbsolutePath();
        rootPath = rootPath.substring(0,rootPath.length()-1);
        configPath = rootPath + "src\\RTMExamples\\SeqIO\\rtc.conf";
        //
        String args[] = {"-f", configPath };
        try {
            manager = new Manager();
            manager.initManager(args);
            manager.initLogger();
            manager.initORB();
            manager.initNaming();
            manager.initExecContext();
            manager.initTimer();
        } catch (Exception e) {
            manager = null;
        }
        SeqInComp init = new SeqInComp();
        manager.setModuleInitProc(init);
        manager.activateManager();
        String component_conf[] = {
                "implementation_id", "SeqIn",
                "type_name",         "SequenceInComponent",
                "description",       "Sequence InPort component",
                "version",           "1.0",
                "vendor",            "Noriaki Ando, AIST",
                "category",          "example",
                "activity_type",     "DataFlowComponent",
                "max_instance",      "10",
                "language",          "C++",
                "lang_type",         "compile",
                ""
                };
      Properties prop = new Properties(component_conf);
      manager.registerFactory(prop, new SeqIn(), new SeqIn());
      comp = manager.createComponent("SeqIn");
    }
    protected void tearDown() throws Exception {
        for(int intIdx=0;intIdx<manager.m_ecs.size();intIdx++) {
            manager.m_ecs.elementAt(intIdx).stop();
            Thread.yield();
        }
        Thread.sleep(600);
        manager.shutdownComponents();
        manager.shutdownNaming();
        manager = null;
        super.tearDown();
    }

    /**
     *<pre>
     * ComponentProfileのチェック
     *　・設定したComponentProfileが取得できるか？
     *　・設定したPort情報を取得できるか？
     *　・設定したPortProfileを取得できるか？
     *</pre>
     */
    public void test_profile() {
        ComponentProfile prof = comp.get_component_profile();
//        assertEquals("ConsoleIn0", comp.get_component_profile().instance_name);
        assertEquals("SequenceInComponent", comp.get_component_profile().type_name);
        assertEquals("Sequence InPort component", comp.get_component_profile().description);
        assertEquals("1.0", comp.get_component_profile().version);
        assertEquals("Noriaki Ando, AIST", comp.get_component_profile().vendor);
        assertEquals("example", comp.get_component_profile().category);
        //
        PortListHolder portlist = new PortListHolder(comp.get_ports());
        assertEquals( 8, portlist.value.length);
        assertEquals( "Short", portlist.value[0].get_port_profile().name);
        assertEquals( "Long", portlist.value[1].get_port_profile().name);
        assertEquals( "Float", portlist.value[2].get_port_profile().name);
        assertEquals( "Double", portlist.value[3].get_port_profile().name);
        assertEquals( "ShortSeq", portlist.value[4].get_port_profile().name);
        assertEquals( "LongSeq", portlist.value[5].get_port_profile().name);
        assertEquals( "FloatSeq", portlist.value[6].get_port_profile().name);
        assertEquals( "DoubleSeq", portlist.value[7].get_port_profile().name);
        //
        Properties prop = new Properties();
        this.copyToProperties(prop, new NVListHolder(portlist.value[0].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedShort", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[1].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedLong", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[2].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedFloat", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[3].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedDouble", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[4].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedShortSeq", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[5].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedLongSeq", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[6].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedFloatSeq", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
        this.copyToProperties(prop, new NVListHolder(portlist.value[7].get_port_profile().properties));
        assertEquals( "DataInPort", prop.getProperty("port.port_type"));
        assertEquals( "TimedDoubleSeq", prop.getProperty("dataport.data_type"));
        assertEquals( "CORBA_Any", prop.getProperty("dataport.interface_type"));
        assertEquals( "Push, Pull", prop.getProperty("dataport.dataflow_type"));
        assertEquals( "Any", prop.getProperty("dataport.subscription_type"));
        //
    }
    
    /**
     *<pre>
     * ExecutionContextのチェック
     *  ・RTCのalive状態を取得できるか？
     *　・ExecutionContextの実行状態を取得できるか？
     *　・ExecutionContextの種類を取得できるか？
     *　・ExecutionContextの更新周期を設定できるか？
     *　・ExecutionContextの更新周期を取得できるか？
     *　・ExecutionContextを停止できるか？
     *　・停止したExecutionContextを再度停止した場合にエラーが返ってくるか？
     *　・ExecutionContextを開始できるか？
     *　・開始したExecutionContextを再度開始した場合にエラーが返ってくるか？
     *</pre>
     */
    public void test_EC() {
        assertEquals(true, comp.is_alive());
        ExecutionContextListHolder execlist = new ExecutionContextListHolder();
        execlist.value = comp.get_contexts();
        assertEquals(true, execlist.value[0].is_running());
        assertEquals(ExecutionKind.PERIODIC, execlist.value[0].get_kind());
        assertEquals(1000.0, execlist.value[0].get_rate());
        //
        ReturnCode_t result = execlist.value[0].stop();
        assertEquals(ReturnCode_t.RTC_OK, result);
        assertEquals(false, execlist.value[0].is_running());
        result = execlist.value[0].stop();
        assertEquals(ReturnCode_t.PRECONDITION_NOT_MET, result);
        //
        result = execlist.value[0].start();
        assertEquals(ReturnCode_t.RTC_OK, result);
        assertEquals(true, execlist.value[0].is_running());
        result = execlist.value[0].start();
        assertEquals(ReturnCode_t.PRECONDITION_NOT_MET, result);
        //
    }

    /**
     *<pre>
     * RTCのチェック
     *  ・RTCの状態を取得できるか？
     *　・Inactive状態でdeactivateした場合にエラーが返ってくるか？
     *　・RTCをactivateできるか？
     *　・Active状態でactivateした場合にエラーが返ってくるか？
     *　・Active状態でresetした場合にエラーが返ってくるか？
     *　・Active状態でfinalizeした場合にエラーが返ってくるか？
     *　・RTCをfinalizeできるか？
     *　・RTCをfinalizeしてもalive状態か？
     *　・RTCをexitしたらalive状態から抜けるか？
     *</pre>
     */
    public void test_State() {
        ExecutionContextListHolder execlist = new ExecutionContextListHolder();
        execlist.value = comp.get_contexts();
        assertEquals(LifeCycleState.INACTIVE_STATE, execlist.value[0].get_component_state(comp.getObjRef()));
        ReturnCode_t result = execlist.value[0].deactivate_component(comp.getObjRef());
        assertEquals(ReturnCode_t.PRECONDITION_NOT_MET, result);
        result = execlist.value[0].start();
        //
        result = execlist.value[0].activate_component(comp.getObjRef());
        Thread.yield();
        assertEquals(LifeCycleState.ACTIVE_STATE, execlist.value[0].get_component_state(comp.getObjRef()));
        result = execlist.value[0].activate_component(comp.getObjRef());
        assertEquals(ReturnCode_t.PRECONDITION_NOT_MET, result);
        assertEquals(LifeCycleState.ACTIVE_STATE, execlist.value[0].get_component_state(comp.getObjRef()));
        //
        result = execlist.value[0].reset_component(comp.getObjRef());
        assertEquals(ReturnCode_t.PRECONDITION_NOT_MET, result);
        //
        result = comp._finalize();
        assertEquals(ReturnCode_t.PRECONDITION_NOT_MET,  result);
        //
        result = execlist.value[0].stop();
        result = comp._finalize();
        assertEquals(ReturnCode_t.RTC_OK,  result);
        assertEquals(true, comp.is_alive());
        result = comp.exit();
        assertEquals(false, comp.is_alive());
    }

}

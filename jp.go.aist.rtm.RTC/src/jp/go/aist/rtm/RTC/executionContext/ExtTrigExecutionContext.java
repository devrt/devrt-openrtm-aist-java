package jp.go.aist.rtm.RTC.executionContext;

import jp.go.aist.rtm.RTC.Manager;
import jp.go.aist.rtm.RTC.log.Logbuf;
import jp.go.aist.rtm.RTC.util.TimeValue;

import org.omg.CORBA.SystemException;
/**
 * {@.ja ステップ実行が可能な ExecutionContext クラス}
 * {@.en ExecutionContext class that enables one step execution}
 * <p>
 * {@.ja １周期毎の実行が可能なPeriodic Sampled Data Processing(周期実行用)
 * ExecutionContextクラスです。
 * 外部からのメソッド呼びだしによって時間が１周期づつ進みます。}
 * {@.en ExecutionContext class that can execute every one cycle for Periodic
 * Sampled Data Processing.
 * Time(Tick) advances one cycle by invoking method externally.}
 */
public class ExtTrigExecutionContext
        extends PeriodicExecutionContext implements Runnable {

    /**
     * {@.ja コンストラクタ}
     * {@.en Constructor}
     */
    public ExtTrigExecutionContext() {
        super();

        rtcout = new Logbuf("Manager.ExtTrigExecutionContext");

    }
    
    /**
     * <p>ExecutionContextの処理を１周期分進めます。</p>
     */
    public void tick() throws SystemException {

        rtcout.println(Logbuf.TRACE, "ExtTrigExecutionContext.tick()");

        synchronized (m_worker) {
            m_worker._called = true;
            m_worker.notifyAll();
        }
    }

    /**
     * <p>ExecutionContextにattachされている各Componentの処理を呼び出します。
     * 全Componentの処理を呼び出した後、次のイベントが発生するまで休止します。</p>
     */
    public int svc() {

        rtcout.println(Logbuf.TRACE, "ExtTrigExecutionContext.svc()");

        do {
            TimeValue tv = new TimeValue(0, m_usec); // (s, us)

            synchronized (m_worker) {
                while (!m_worker._called && m_running) {
                    try {
                        m_worker.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (m_worker._called) {
                    m_worker._called = false;
                    for (int intIdx = 0; intIdx < m_comps.size(); ++intIdx) {
                        m_comps.elementAt(intIdx).invoke();
                    }
                    while (!m_running) {
                        try {
                            Thread.sleep(0, (int) tv.getUsec());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(0, (int) tv.getUsec());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while (m_running);
        
        return 0;
    }

    /**
     * <p>ExecutionContextを起動します。</p>
     */
    public void run() {
        this.svc();
    }

    private class Worker {
        
        public Worker() {
            this._called = false;
        }

        public boolean _called;
    }
    
    private Worker m_worker = new Worker();

    /**
     * <p>このExecutionContextを生成するFactoryクラスを
     * ExecutionContext用ObjectManagerに登録します。</p>
     * 
     * @param manager Managerオブジェクト
     */
    public static void ExtTrigExecutionContextInit(Manager manager) {
        manager.registerECFactory("jp.go.aist.rtm.RTC.executionContext.ExtTrigExecutionContext");
    }
    
    /**
     * <p>ExecutionContextのインスタンスを破棄します。</p>
     * 
     * @param comp 破棄対象ExecutionContextインスタンス
     */
    public Object ECDeleteFunc(ExecutionContextBase comp) {
        return null;
    }

    /**
     * <p>ExecutionContextのインスタンスを取得します。</p>
     * 
     * @return ExecutionContextインスタンス
     */
    public ExecutionContextBase ECNewFunc() {
        return this;
    }
    
    /**
     * <p>Logging用フォーマットオブジェクト</p>
     */
    protected Logbuf rtcout;
}

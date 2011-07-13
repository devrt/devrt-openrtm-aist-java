package jp.go.aist.rtm.RTC.buffer;

import jp.go.aist.rtm.RTC.BufferFactory;
import jp.go.aist.rtm.RTC.ObjectCreator;
import jp.go.aist.rtm.RTC.ObjectDestructor;

import org.omg.CORBA.portable.OutputStream;

/**
 *  <p> CdrRingBuffer </p>
 *
 */
public class CdrRingBuffer implements ObjectCreator<BufferBase<OutputStream>>, ObjectDestructor {
    
    /**
     * {@.ja RingBuffer$B$r@8@.$9$k!#(B}
     * {@.en Creats RingBuffer.}
     * 
     * @return 
     *   {@.ja $B@8@.$7$?%$%s%9%?%s%9$N(BRingBuffer}
     *   {@.en Object Created RingBuffer}
     *
     */
    public BufferBase<OutputStream> creator_() {
        return new RingBuffer<OutputStream>();
    }
    /**
     * {@.ja $B%$%s%9%?%s%9$rGK4~$9$k!#(B}
     * {@.en Destroys the object.}
     * 
     * @param obj    
     *   {@.ja $BGK2u$9$k%$%s%9%?%s%9(B}
     *   {@.en The target instances for destruction}
     *
     */
    public void destructor_(Object obj) {
        obj = null;
    }

    /**
     * {@.ja $B=i4|2==hM}!#(B}
     * {@.en Initialization}
     * <p>
     * {@.ja $B%U%!%/%H%j$X%*%V%8%'%/%H$rDI2C$9$k!#(B}
     * {@.en Adds the object to the factory.}
     *
     */
    public static void CdrRingBufferInit() {
        final BufferFactory<RingBuffer<OutputStream>,String> factory 
            = BufferFactory.instance();

        factory.addFactory("ring_buffer",
                    new CdrRingBuffer(),
                    new CdrRingBuffer());
    
    }
}


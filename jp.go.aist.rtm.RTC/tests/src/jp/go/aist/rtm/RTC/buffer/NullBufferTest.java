package jp.go.aist.rtm.RTC.buffer;

import jp.go.aist.rtm.RTC.buffer.BufferBase;
import jp.go.aist.rtm.RTC.buffer.NullBuffer;
import jp.go.aist.rtm.RTC.util.DataRef;
import junit.framework.TestCase;

/**
 * <p>
 * NullBufferクラスのためのテストケースです。
 * </p>
 */
public class NullBufferTest extends TestCase {

    private BufferBase<Integer> m_intBuf = new MyBuffer<Integer>();
    private BufferBase<Character> m_charBuf = new MyBuffer<Character>();
    private BufferBase<Data> m_dataBuf = new MyBuffer<Data>();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     *<pre>
     *　int型データのwrite/readチェック
     *　 ・int型データを書き込めるか？
     *　 ・書き込んだint型データ正常にを読み出せるか？
     *</pre>
     */
    public void test_wr_int() throws Exception {
        
        for (int i = 0; i < 100; i++) {
            
            // 書き込みが成功することを確認する
            assertTrue(this.m_intBuf.write(i));
            
            // 書き込んだデータを読み出して、書き込んだデータと一致することを確認する
            DataRef<Integer> intvar = new DataRef<Integer>(0);
            this.m_intBuf.read(intvar);
            assertEquals(i, intvar.v.intValue());
        }
    }
    
    /**
     *<pre>
     *　char型データのwrite/readチェック
     *　 ・char型データを書き込めるか？
     *　 ・書き込んだchar型データ正常にを読み出せるか？
     *</pre>
     */
    public void test_wr_char() throws Exception {
        
        for (char c = 0; c < 100; c++) {
            
            // 書き込みが成功することを確認する
            assertTrue(this.m_charBuf.write(c));
            
            // 書き込んだデータを読み出して、書き込んだデータと一致することを確認する
            DataRef<Character> charvar = new DataRef<Character>('\n');
            this.m_charBuf.read(charvar);
            assertEquals(c, charvar.v.charValue());
        }
    }

    /**
     *<pre>
     *　オブジェクトデータのwrite/readチェック
     *　 ・オブジェクトデータを書き込めるか？
     *　 ・書き込んだオブジェクトデータ正常にを読み出せるか？
     *</pre>
     */
    public void test_wr_struct() throws Exception {
        
        for (int i = 0; i < 100; i++) {
            
            Data data = new Data();
            data.int_data = i;
            data.double_data = i / 3.141592653589793238462643383279;
            
            // 書き込みが成功することを確認する
            assertTrue(this.m_dataBuf.write(data));
            
            // 書き込んだデータを読み出して、書き込んだデータと一致することを確認する
            Data expected = (Data) data.clone();
            DataRef<Data> dvar = new DataRef<Data>(new Data());
            this.m_dataBuf.read(dvar);
            assertEquals(expected, dvar.v);
        }
    }

    /**
     *<pre>
     * 　isFull()メソッドのチェック
     * 　 ・初期状態でフルではないか？
     *</pre>
     */
    public void test_isFull() {
        
        // 初期状態でフルではないことを確認する
        assertFalse(this.m_intBuf.isFull());
        assertFalse(this.m_charBuf.isFull());
        assertFalse(this.m_dataBuf.isFull());
    }

    /**
     *<pre>
     *　isFull()メソッドのチェック
     *　 ・バッファ長さを越える書き込みを行っても決してフルにならないか？
     *</pre>
     *
     */
    public void test_isFull_NeverFull() {
        
        // バッファ長さを越えるデータを書き込む
        for (int i = 0; i < this.m_intBuf.length() + 100; i++) {
            this.m_intBuf.write(new Integer(12345 + i));
        }
        
        // フルになっていないことを確認する
        assertFalse(this.m_intBuf.isFull());
    }
    
    /**
     *<pre>
     *　isEmpty()メソッドのチェック
     *　 ・初期状態で空ではないか？
     *</pre>
     */
    public void test_isEmpty() {
        
        assertFalse(this.m_intBuf.isEmpty());
        assertFalse(this.m_charBuf.isEmpty());
        assertFalse(this.m_dataBuf.isEmpty());
        
    }

    /**
     *<pre>
     *　isEmpty()メソッドのチェック
     *　 ・バッファ長さを越えるデータの読み込みを行っても空にならないか？
     *</pre>
     */
    public void test_isEmpty_NeverEmpty() {

        // バッファ長を超えるデータを読み出す
        DataRef<Integer> dataRef = new DataRef<Integer>(0);
        for (int i = 0; i < this.m_intBuf.length() + 100; i++) {
            this.m_intBuf.read(dataRef);
        }

        // 空ではないことを確認する
        assertFalse(this.m_intBuf.isEmpty());
    }
    
    /**
     *<pre>
     *　int型データのput/getチェック
     *　 ・書き込んだデータを正常に取得できるか？
     *</pre>
     */
    public void test_pg_int() {
        
        for (int i = 0; i < 100; i++) {
            
            MyBuffer<Integer> intBuf = (MyBuffer<Integer>) this.m_intBuf;
            
            // データを書き込む
            intBuf.put(i);

            // 書き込んだデータを読み出して、書き込んだデータと一致することを確認する
            int result = intBuf.get();
            assertEquals(i, result);
        }
    }

    /**
     *<pre>
     *　char型データのput/getチェック
     *　 ・書き込んだデータを正常に取得できるか？
     *</pre>
     */
    public void test_pg_char() {
        
        for (char c = 0; c < 100; c++) {
            
            MyBuffer<Character> charBuf = (MyBuffer<Character>) this.m_charBuf;
            
            // データを書き込む
            charBuf.put(c);
            
            // 書き込んだデータを読み出して、書き込んだデータと一致することを確認する
            char result = charBuf.get();
            assertEquals(c, result);
        }
    }

    /**
     *<pre>
     *　 オブジェクト型データのput/getチェック
     *　 ・書き込んだデータを正常に取得できるか？
     *</pre>
     */
    public void test_put_struct() {
        
        for (int i = 0; i < 100; i++) {
            
            Data data = new Data();
            data.int_data = i;
            data.double_data = i / 3.141592653589793238462643383279;
            
            MyBuffer<Data> dataBuf = (MyBuffer<Data>) this.m_dataBuf;
            
            // データを書き込む
            dataBuf.put(data);
            
            // 書き込んだデータを読み出して、書き込んだデータと一致することを確認する
            Data expected = (Data) data.clone();
            Data result = dataBuf.get();
            assertEquals(expected, result);
        }
    }
    
    class Data implements Cloneable {
        
        public int int_data;
        public double double_data;

        public Object clone() {
            
            Data clone = new Data();
            clone.int_data = this.int_data;
            clone.double_data = this.double_data;
            
            return clone;
        }
        
        public boolean equals(Object rhs) {
            return equals((Data) rhs);
        }
        
        public boolean equals(Data rhs) {
            
            return (this.int_data == rhs.int_data) && (this.double_data == rhs.double_data);
        }
    }
    
    class MyBuffer<DataType> extends NullBuffer<DataType> {
        
        public void put(final DataType data) {
            super.put(data);
        }
        
        public DataType get() {
            return super.get();
        }
    }
}

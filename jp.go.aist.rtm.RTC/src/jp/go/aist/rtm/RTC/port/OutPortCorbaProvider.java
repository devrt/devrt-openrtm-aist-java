package jp.go.aist.rtm.RTC.port;

import jp.go.aist.rtm.RTC.buffer.BufferBase;
import jp.go.aist.rtm.RTC.util.CORBA_SeqUtil;
import jp.go.aist.rtm.RTC.util.DataRef;
import jp.go.aist.rtm.RTC.util.NVUtil;
import jp.go.aist.rtm.RTC.util.POAUtil;
import jp.go.aist.rtm.RTC.util.TypeCast;

import org.omg.CORBA.Any;

import RTC.OutPortAny;
import RTC.OutPortAnyHelper;
import RTC.OutPortAnyPOA;
import _SDOPackage.NVListHolder;

/**
 * <p>CORBAを通信手段とする出力ポートプロバイダの実装です。</p>
 *
 * @param <DataType> データ型を指定します。
 */
public class OutPortCorbaProvider<DataType>
    extends OutPortAnyPOA implements OutPortProvider {
    /**
     * <p>コンストラクタです。</p>
     * 
     * @param DATA_TYPE_CLASS 型パラメータで指定したデータ型のClassオブジェクト
     * @param buffer 当該プロバイダに割り当てるバッファ
     */
    public OutPortCorbaProvider(Class<DataType> DATA_TYPE_CLASS,
            BufferBase<DataType> buffer) throws Exception {
        
        this.TYPE_CAST = new TypeCast<DataType>(DATA_TYPE_CLASS);
        this.m_buffer = buffer;

        // PortProfile setting
        this.m_outPortProvider.setDataType(this.TYPE_CAST.getDataTypeCodeName());
        this.m_outPortProvider.setInterfaceType("CORBA_Any");
        this.m_outPortProvider.setDataFlowType("Push, Pull");
        this.m_outPortProvider.setSubscriptionType("Flush, New, Periodic");
        
        // ConnectorProfile setting
        m_objref = this._this();
        CORBA_SeqUtil.push_back(this.m_outPortProvider.m_properties,
                NVUtil.newNV("dataport.corba_any.outport_ref", this.m_objref));
    }
    
    /**
     * <p>当該プロバイダのCORBAオブジェクト参照を取得します。</p>
     * 
     * @return 当該プロバイダのCORBAオブジェクト参照
     */
    public OutPortAny _this() {
        
        if (this.m_objref == null) {
            try {
                this.m_objref = OutPortAnyHelper.narrow(POAUtil.getRef(this));
                
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        
        return this.m_objref;
    }

    /**
     * <p>内部バッファからデータを読み出します。</p>
     * 
     * @return 読み出したデータ
     */
    public Any get() {
        
        DataRef<DataType> data = new DataRef<DataType>(null);
        this.m_buffer.read(data);
        
        return TYPE_CAST.castAny(data.v);
    }
    
    /**
     * <p>インタフェースプロファイルを取得します。</p>
     * 
     * @param properties インタフェースプロファイルを受け取るNVListHolderオブジェクト
     */
    public void publishInterfaceProfile(NVListHolder properties) {

        this.m_outPortProvider.publishInterfaceProfile(properties);
    }
    
    /**
     * <p>接続プロファイルを取得します。指定されたNVListHolder内に保持されている
     * インタフェースタイプと照合して、タイプが一致する場合にのみ取得されます。</p>
     * 
     * @param properties 接続プロファイルを受け取るNVListHolderオブジェクトを指定します。
     * あらかじめ、dataport.interface_typeにインタフェースタイプを設定しておく必要があります。
     */
    public void publishInterface(NVListHolder properties) {

        this.m_outPortProvider.publishInterface(properties);
    }

    private BufferBase<DataType> m_buffer;
    private OutPortAny m_objref;
    private TypeCast<DataType> TYPE_CAST;
    private OutPortProviderImpl m_outPortProvider = new OutPortProviderImpl();

}

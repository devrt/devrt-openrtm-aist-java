package jp.go.aist.rtm.RTC;

import _SDOPackage.ServiceProfile;
import _SDOPackage.SDOServicePOA;
  /**
   * {@.ja SdoServiceProviderBase インターフェース。}
   * {@.en SdoServiceProviderBase Interface.}
   * <p>
   * {@.ja SDOで定義されているSDOサービスのプロバイダを実装するための基底クラ
   * ス。SDOサービスには、外部から提供サービスをRTC(SDO)側で利用する
   * SDOサービスコンシューマと、RTC(SDO)自身がSDOサービスを提供するSDO
   * サービスプロバイダがある。すべてのSDOサービスプロバイダはこの基底
   * クラスを継承して実装される。
   *
   * このオブジェクトのライフサイクルは以下の通り。
   *
   * -# オブジェクトは通常、共有オブジェクト (so, DLL) としてコンパイル・
   *    リンクされる。
   * -# マネージャに対してロードされるとモジュール初期化関数によりオブ
   *    ジェクトファクトリが、SdoServiceProviderFactory に対して登録さ
   *    れる。登録のキーにはサービスインターフェースの IFR (interface
   *    repository) ID が利用され、これによりサービスが区別される。
   * -# rtc.conf等のコンフィギュレーション指定により、有効化することが
   *    指定されているサービスインプロバイダは、RTCの起動と同時にインス
   *    タンス化される。
   * -# インスタンス化後、初期化関数 init() が呼ばれる。引数には当該サー
   *    ビスのためのコンフィギュレーションオプションが coil::Propertyに
   *    より渡される。
   * -# インスタンス化されたSDOサービスプロバイダは
   *    SDO::get_get_sdo_service() により外部からアクセスされる。このと
   *    き、サービスを指定するIDはIFR IDと同じである。このときのアタッ
   *    チシーケンスは以下の通り。
   * -# RTCがfinalizeされ解体されると同時にSDOサービスプロバイダも解体
   *    されるが、その際にはSdoServiceProviderBase::finalize()がコール
   *    されるので、ここでリソースの解放など終了処理を行う。
   *
   * <pre>
   * 
   *   [RTC]      [SDO service]               [Other]
   *     |              :                        |
   *     | instantiate  :                        |
   *     |------------->:                        |
   *     |    init()    |                        |
   *     |------------->|                        |
   *     |              | get_service_profiles() |
   *     |<--------------------------------------|
   *     |              |    get_sdo_service()   |
   *     |<--------------------------------------|
   *     |              |        use service     |
   *     |              |<-----------------------|
   *     |              |                        |
   *     |  finalize()  |                        |
   *     |------------->x                        |
   *     x              x                        |
   *
   * </pre>
   *
   * このクラスの実装に当たっては、少なくとも以下の純粋仮想関数を実装す
   * る必要がある。
   *
   * - init(): 初期化関数。与えられた RTObject および ServiceProfile か
   *   ら、当該オブジェクトを初期化する。
   * - reinit(): 再初期化関数。ServiceProfile は設定情報更新のため同一
   *   IDで呼び出されることが有るが、その際にこの関数が新たな
   *   ServiceProfile とともに呼び出される。関数内では、設定の変更など
   *   再初期化処理を実装する。
   * - getProfile(): 設定されたプロファイルを返す関数。
   * - finalize(): 終了処理。コンシューマがデタッチされる際に呼び出され
   *   る関数。関数内では終了処理を実装する。
   *
   * SdoServiceProvider は通常共有オブジェクトとしてコンパイル・リンク
   * される。共有オブジェクトのエントリポイントは通常コンパイルされたファ
   * イル名の basename + "Init" にしておく。以下に、クラス名、ファイル
   * 名、エントリポイント関数名の推奨例を示す。
   *
   * - 実装クラス名: MySdoServiceConusmer 
   * - ファイル名: MySdoServiceProvider.h. MySdoServiceProvider.cpp
   * - 共有オブジェクト名: MySdoServiceProvider.so (or DLL)
   * - エントリポイント関数名: MySdoServiceProviderInit()
   *
   * エントリポイント関数は通常以下のように、SdoServiceProviderFactory
   * に当該コンシューマのファクトリ (と解体ファンクタ) を登録する以下の
   * ような関数になる。
   *
   * <pre>
   * extern "C"
   * {
   *   void MySdoServiceProviderInit()
   *   {
   *     RTC::SdoServiceProviderFactory& factory
   *       = RTC::SdoServiceProviderFactory::instance();
   *     factory.addFactory(CORBA_Util::toRepositoryId<OpenRTM::MySdoService>(),
   *                        ::coil::Creator< ::RTC::SdoServiceProviderBase,
   *                        ::RTC::MySdoServiceProvider>,
   *                        ::coil::Destructor< ::RTC::SdoServiceProviderBase,
   *                        ::RTC::MySdoServiceProvider>);
   *   }
   * };
   * </pre>}
   * 
   *
   *
   */ 


//public interface SdoServiceProviderBase extends SDOServicePOA{
public interface SdoServiceProviderBase {

    /**
     * {@.ja コンシューマクラスの初期化関数}
     * {@.en Initialization function of the consumer class}
     * <p>
     * {@.ja このオブジェクトの初期化を行う。外部からSDOサービスが
     * ServiceProfile とともにアタッチされると、SDOコンシューマがインス
     * タンス化され、その直後に SDO サービスがアタッチされた RTC と与え
     * られた ServiceProfile を引数としてこの関数が呼ばれる。
     *
     * 関数内では、ServiceProfile 内の SDO サービスリファレンスを
     * CorbaProvider クラス等を利用しオブジェクト内に保持するとともに、
     * properties から設定内容を読み込みサービス固有の設定等を行う。与
     * えられたサービスのオブジェクトリファレンスが不正、あるいは
     * properties の内容が不正、等の場合は戻り値に false を返す。}
     *
     * @param rtobj 
     *   {@.ja このオブジェクトがインスタンス化された RTC}
     * @param profile 
     *   {@.ja 外部から与えられた SDO ServiceProfile}
     * @return 
     *   {@.ja 与えられた SDO Service や ServiceProfile が不正の場合 false}
     *
     */
    public boolean init(RTObject_impl rtobj,
                      final _SDOPackage.ServiceProfile profile);
    /**
     * {@.ja コンシューマクラスの再初期化関数}
     * {@.en Reinitialization function of the consumer class}
     * <p>
     * {@.ja このオブジェクトの再初期化を行う。ServiceProfile には id フィー
     * ルドにセッション固有の UUID がセットされているが、同一の id の場
     * 合、properties に設定された設定情報の変更や、service フィールド
     * のサービスの参照の変更が行われる。その際に呼ばれるのがこの
     * reinit() 関数である。実装では、service フィールドのオブジェクト
     * リファレンスの同一性を確認し、異なっている場合保持しているリファ
     * レンスを更新する必要がある。また properties には新たな設定が与え
     * られている可能性があるので、内容を読み込み設定を更新する。}
     *
     * @param profile 
     *   {@.ja 新たに与えられた SDO ServiceProfile}
     * @return 
     *   {@.ja 不正な ServiceProfile が与えられた場合は false}
     *
     *
     */
    public boolean reinit(final _SDOPackage.ServiceProfile profile);

    /**
     * {@.ja ServiceProfile を返す}
     * {@.en Getting ServiceProfile}
     * <p>
     * {@.ja init()/reinit()で与えられた ServiceProfile は通常オブジェクト内
     * で保持される。SDO Service 管理フレームワークは管理上このオブジェ
     * クトに対応する ServiceProfile を必要とするので、この関数では保持
     * されている ServiceProfile を返す。}
     * 
     * @return 
     *  {@.ja このオブジェクトが保持している ServiceProfile}
     *
     */
    public _SDOPackage.ServiceProfile getProfile();

    /**
     * {@.ja 終了処理}
     * {@.en Finalization}
     * <p>
     * {@.ja SDOサービスがでタッチされる際に呼び出される終了処理用関数。サー
     * ビスのでタッチに際して、当該オブジェクトが保持するリソースを解放
     * するなどの処理を行う。}
     *
     *
     */
    public void finalize();

}

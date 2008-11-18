package jp.go.aist.rtm.RTC;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jp.go.aist.rtm.RTC.util.Properties;
import jp.go.aist.rtm.RTC.util.StringUtil;

/**
* <p>モジュール管理クラスです。モジュールのロード・アンロードなどを管理します。</p>
*/
public class ModuleManager {
    
    private final String CONFIG_EXT = "manager.modules.config_ext";
    private final String CONFIG_PATH = "manager.modules.config_path";
    private final String DETECT_MOD = "manager.modules.detect_loadable";
    private final String MOD_LOADPTH = "manager.modules.load_path";
    private final String INITFUNC_SFX = "manager.modules.init_func_suffix";
    private final String INITFUNC_PFX = "manager.modules.init_func_prefix";
    private final String ALLOW_ABSPATH = "manager.modules.abs_path_allowed";
    private final String ALLOW_URL = "manager.modules.download_allowed";
    private final String MOD_DWNDIR = "manager.modules.download_dir";
    private final String MOD_DELMOD = "manager.modules.download_cleanup";
    private final String MOD_PRELOAD = "manager.modules.preload";

    /**
     * <p>コンストラクタです。
     * 指定されたPropertiesオブジェクト内の情報に基づいて初期化を行います。</p>
     * 
     * @param properties 初期化情報を持つPropertiesオブジェクト
     */
    public ModuleManager(Properties properties) {
        
        m_properties = properties;
        
        m_configPath = new Vector<String>();
        String[] configPath = properties.getProperty(CONFIG_PATH).split(",");
        for (int i = 0; i < configPath.length; i++) {
            m_configPath.add(configPath[i].trim());
        }
        
        m_loadPath = new Vector<String>();
        String[] loadPath = properties.getProperty(MOD_LOADPTH).split(",");
        for (int i = 0; i < configPath.length; i++) {
            m_loadPath.add(loadPath[i].trim());
        }
        
        m_absoluteAllowed = StringUtil.toBool(
                properties.getProperty(ALLOW_ABSPATH), "yes", "no", false);
        m_downloadAllowed = StringUtil.toBool(
                properties.getProperty(ALLOW_URL), "yes", "no", false);
        
        m_initFuncSuffix = properties.getProperty(INITFUNC_SFX);
        m_initFuncPrefix = properties.getProperty(INITFUNC_PFX);
    }
    
    /**
     * <p>デストラクタです。ロード済みモジュールのアンロードなど、リソースの解放処理を行います。
     * 当該ModuleManagerオブジェクトの使用を終えた際に、明示的に呼び出してください。</p>
     */
    public void destruct() {
        unloadAll();
    }
    
    /**
     * <p>ファイナライザです。</p>
     */
    protected void finalize() throws Throwable {
        
        try {
            destruct();
            
        } finally {
            super.finalize();
        }
    }

    /**
     * <p>指定されたモジュールをロードします。初期化メソッドを指定した場合には、
     * ロード時にそのメソッドが呼び出されます。これにより、モジュール初期化を行えます。</p>
     * 
     * <p>コンストラクタで指定した初期化情報の 'manager.modules.abs_path_allowed' が 'yes' の場合は、
     * className引数は、ロードモジュールのフルクラス名として解釈されます。<br />
     * 'no' が指定されている場合は、className引数はロードモジュールのシンプルクラス名として解釈され、
     * 規定のモジュールロードパス以下からモジュールが検索されます。</p>
     * 
     * <p>コンストラクタで指定した初期化情報の 'manager.modules.download_allowed' が 'yes' の場合は、
     * className引数は、ロードモジュールのURLとして解釈されます。（未実装）</p>
     * 
     * @param moduleName モジュール名
     * @param methodName 初期実行メソッド名
     * @return moduleName引数で指定したモジュール名がそのまま返されます。
     * @throws IllegalArgumentException 引数が正しく指定されていない場合にスローされます。
     */
    public String load(final String moduleName, final String methodName)
            throws Exception {
        
        if (moduleName == null || moduleName.length() == 0) {
            throw new IllegalArgumentException("moduleName is empty.:load()");
        }
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException("methodName is empty.:load()");
        }

        try {
            new URL(moduleName);
            if (! m_downloadAllowed) {
                throw new IllegalArgumentException("Downloading module is not allowed.");
                
            } else {
                throw new IllegalArgumentException("Not implemented.");
            }
        } catch (MalformedURLException moduleName_is_not_URL) {
        }

        Class target = null;
        if (m_absoluteAllowed) {
            try {
                target = Class.forName(moduleName);
                
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new ClassNotFoundException("Not implemented.", e);
            }
        } else {
            for (int i = 0; i < m_loadPath.size(); i++) {
                String fullClassName = m_loadPath.elementAt(i) + "." + moduleName;
                try {
                    target = Class.forName(fullClassName);
                    
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    // throw new ClassNotFoundException("Not implemented.", ex);
                }
            }
        }

        Method initMethod;
        try {
            initMethod = target.getMethod(methodName);
            
        } catch (SecurityException e) {
            e.printStackTrace();
            throw e;
            
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw e;
        }

        try {
            initMethod.invoke(target.newInstance());
            
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw e;
            
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw e;
            
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw e;
            
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw e;
        }
        
        return moduleName;
    }
    
    /**
     * <p>指定されたモジュールをアンロードします。</p>
     * <p>※未実装</p>
     * 
     * @param moduleName アンロードするモジュール名
     */
    public void unload(String moduleName) {
    }
    
    /**
     * <p>すべてのモジュールをアンロードします。</p>
     * <p>※未実装</p>
     */
    public void unloadAll() {
    }
    
    /**
     * <p>規定となるモジュールロードパスを指定します。</p>
     * 
     * @param loadPath 規定ロードパス
     * @see 規定ロードパスの使われ方については、load()を参照してください。
     */
    public void setLoadpath(final Vector<String> loadPath) {
        m_loadPath = new Vector<String>(loadPath);
    }
    
    /**
    * <p>規定となるモジュールロードパスを取得します。</p>
    *
    * @return 規定モジュールロードパス
    */
    public Vector<String> getLoadPath() {
        return new Vector<String>(m_loadPath);
    }

    /**
     * <p>規定となるモジュールロードパスを追加します。</p>
     * 
     * @param loadPath 追加する規定ロードパス
     */
    public void addLoadPath(final Vector<String> loadPath) {
        m_loadPath.addAll(loadPath);
    }
    
    /**
     * <p>ロード済みのモジュールリストを取得します。</p>
     *
     * @return ロード済みモジュールリスト
     */
    public Vector<String> getLoadedModules() {
        return new Vector<String>(m_modules.values());
    }
        
    /**
     * <p>ロード可能なモジュールリストを取得します。</p>
     * 
     * @return ロード可能なモジュールリスト
     */
    public Vector<String> getLoadableModules() {
        return new Vector<String>();
    }
    
    /**
     * <p>モジュールのフルクラス名指定を指定します。</p>
     */
    public void allowAbsolutePath() {
        m_absoluteAllowed = true;
    }
    
    /**
     * <p>モジュールのフルクラス名指定解除を指定します。</p>
     */
    public void disallowAbsolutePath() {
        m_absoluteAllowed = false;
    }
    
    /**
     * <p>モジュールのダウンロード許可を指定します。</p>
     */
    public void allowModuleDownload() {
        m_downloadAllowed = true;
    }
    
    /**
     * <p>モジュールのダウンロード許可を解除します。</p>
     */
    public void disallowModuleDownload() {
        m_downloadAllowed = false;
    }
    
    /**
     * <p>ModuleManagerプロパティ</p>
     */
    protected Properties m_properties;
    /**
     * <p>ロード済みモジュール</p>
     */
    protected Map<String, String> m_modules = new HashMap<String, String>();
    /**
     * <p>モジュールロードパス</p>
     */
    protected Vector<String> m_loadPath = new Vector<String>();
    /**
     * <p>コンフィギュレーションパス</p>
     */
    protected Vector<String> m_configPath = new Vector<String>();
    /**
     * <p>モジュールダウンロード許可フラグ</p>
     */
    protected boolean m_downloadAllowed;
    /**
     * <p>モジュール絶対パス指定許可フラグ</p>
     */
    protected boolean m_absoluteAllowed;
    /**
     * <p>初期実行関数サフィックス</p>
     */
    protected String m_initFuncSuffix = new String();
    /**
     * <p>初期実行関数プリフィックス</p>
     */
    protected String m_initFuncPrefix = new String();
}

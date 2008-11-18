package jp.go.aist.rtm.RTC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import jp.go.aist.rtm.RTC.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * <p>Managerのコンフィグレーションを表現するクラスです。
 * コマンドライン引数や環境変数、設定ファイルを読み込み・解析してコンフィグレーション情報を生成します。</p>
 * 
 * <p>各設定の優先度は次の通りです。
 * <ol>
 * <li>コマンドラインオプション -f</li>
 * <li>環境変数 RTC_MANAGER_CONFIG</li>
 * <li>デフォルト設定ファイル ./rtc.conf</li>
 * <li>デフォルト設定ファイル /etc/rtc.conf</li>
 * <li>デフォルト設定ファイル /etc/rtc/rtc.conf</li>
 * <li>デフォルト設定ファイル /usr/local/etc/rtc.conf</li>
 * <li>デフォルト設定ファイル /usr/local/etc/rtc/rtc.conf</li>
 * <li>埋め込みコンフィギュレーション値</li>
 * </ol>
 * </p>
 * 
 * <p>ただし、コマンドラインオプション -d が指定された場合は、
 * （たとえ -f で設定ファイルを指定しても）埋め込みコンフィグレーション値を優先的に使用します。</p>
 */
public class ManagerConfig {

    // The list of default configuration file path.
    /**
     * <p>Managerのデフォルト・コンフィグレーションのファイル・パス</p>
     */
    public static final String[] CONFIG_FILE_PATH = {
        "./rtc.conf",
        "/etc/rtc.conf",
        "/etc/rtc/rtc.conf",
        "/usr/local/etc/rtc.conf",
        "/usr/local/etc/rtc/rtc.conf",
        null
    };
    
    /**
     * <p>デフォルト・コンフィグレーションのファイル・パスを識別する環境変数です。</p>
     */
    public static final String CONFIG_FILE_ENV = "RTC_MANAGER_CONFIG";

    /**
     * <p>デフォルトコンストラクタです。
     * ManagerConfigオブジェクトを生成するのみであり、何も処理は行われません。</p>
     */
    public ManagerConfig() {
    }

    /**
     * <p>コンストラクタです。コマンドライン引数を受け取り、コンフィグレーション情報を構成します。</p>
     * 
     * @param args コマンドライン引数
     */
    public ManagerConfig(String[] args) throws Exception {
        init(args);
    }

    /**
     * <p>初期化を行います。コマンドライン引数を受け取り、コンフィグレーション情報を構成します。</p>
     * 
     * <p>コマンドラインオプションには、以下のものを使用できます。
     * <ul>
     * <li>-f filePath : コンフィグレーションファイルのパスを指定します。</li>
     * <li>-l module : ロードするモジュールを指定します。（未実装）</li>
     * <li>-o options : その他のオプションを指定します。（未実装）</li>
     * <li>-d : デフォルトコンフィグレーションを使用します。（未実装）</li>
     * </ul>
     * </p>
     * 
     * @param args コマンドライン引数
     */
    public void init(String[] args) throws Exception {
        parseArgs(args);
    }
    
    /**
     * <p>コンフィグレーション情報をPropertiesオブジェクトの形式で取得します。</p>
     * 
     * @param properties コンフィグレーション情報を受け取って格納するPropertiesオブジェクト
     * @throws FileNotFoundException コンフィグレーションファイルが見つからない場合にスローされます。
     * @throws IOException コンフィグレーションファイル読み取りエラーの場合にスローされます。
     */
    public void configure(Properties properties) throws FileNotFoundException, IOException {
        
        properties.setDefaults(DefaultConfiguration.default_config);
        
        if (findConfigFile()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(this.m_configFile));
                properties.load(reader);
                
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        
        setSystemInformation(properties);
    }
    
    /**
     * <p>コマンドライン引数を解析します。</p>
     * 
     * @param args コマンドライン引数
     * @throws IllegalArgumentException コマンドライン引数を解析できなかった場合にスローされます。
     */
    protected void parseArgs(String[] args) throws IllegalArgumentException {
        
        Options options = new Options();
        options.addOption("f", true, "configuration file");
        options.addOption("l", true, "load module");
        options.addOption("o", true, "other options");
        options.addOption("d", false, "use default configuration");

        CommandLine commandLine;
        try {
            CommandLineParser parser = new BasicParser();
            commandLine = parser.parse(options, args);
            
        } catch (ParseException e) {
            throw new IllegalArgumentException("Could not parse arguments.");
        }

        if (commandLine.hasOption("f")) {
            this.m_configFile = commandLine.getOptionValue("f");
        }
        if (commandLine.hasOption("l")) {
            // do nothing
        }
        if (commandLine.hasOption("o")) {
            // do nothing
        }
        if (commandLine.hasOption("d")) {
            // do nothing
        }
    }
    
    /**
     * <p>使用すべきコンフィグレーションファイルを検索して特定します。
     * すでに特定済みの場合は、そのファイルの存在有無のみをチェックします。</p>
     * 
     * <p>なお、次の順序でコンフィグレーションファイルを検索します。
     * <ol>
     * <li>コマンドラインオプション -f</li>
     * <li>環境変数 RTC_MANAGER_CONFIG</li>
     * <li>デフォルト設定ファイル ./rtc.conf</li>
     * <li>デフォルト設定ファイル /etc/rtc.conf</li>
     * <li>デフォルト設定ファイル /etc/rtc/rtc.conf</li>
     * <li>デフォルト設定ファイル /usr/local/etc/rtc.conf</li>
     * <li>デフォルト設定ファイル /usr/local/etc/rtc/rtc.conf</li>
     * </ol>
     * </p>
     * 
     * @return コンフィグレーションファイル未特定の場合 : 使用すべきコンフィグレーションファイルを検索・特定できた場合はtrueを、さもなくばfalseを返します。<br />
     * コンフィグレーションファイル特定済みの場合 : 特定済みのコンフィグレーションファイルが存在すればtrueを、さもなくばfalseを返します。
     */
    protected boolean findConfigFile() {
        
        // Check existance of configuration file given command arg
        if (! (m_configFile == null || m_configFile.length() == 0)) {
            if (fileExist(m_configFile)) {
                return true;
            }
        }

        // Search rtc configuration file from environment variable
        String env = System.getenv(CONFIG_FILE_ENV);
        if (env != null) {
            if (fileExist(env)) {
                this.m_configFile = env;
                return true;
            }
        }

        // Search rtc configuration file from default search path
        for (int i = 0; CONFIG_FILE_PATH[i] != null; i++) {
            if (fileExist(CONFIG_FILE_PATH[i])) {
                m_configFile = CONFIG_FILE_PATH[i];
                return true;
            }
        }

        return false;
    }
    
    /**
     * <p>システム情報を、指定されたPropertiesオブジェクトに追加します。</p>
     * 
     * @param properties システム情報追加先のPropertiesオブジェクト
     */
    protected void setSystemInformation(Properties properties) {

        String osName = "UNKNOWN";
        String osRelease = "UNKNOWN";
        String osVersion = "UNKNOWN";
        String osArch = "UNKNOWN";
        String hostName = "UNKNOWN";
        String pid = "UNKNOWN";
        
        try {
            java.util.Properties sysInfo = System.getProperties();
            
            // OS名
            osName = sysInfo.getProperty("os.name");
            
            // OSバージョン
            osVersion = sysInfo.getProperty("os.version");
            
            // OSアーキテクチャ
            osArch = sysInfo.getProperty("os.arch");
            
            //プロセスID
            pid = System.getProperty("java.version") + new Random().nextInt();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        
        try {
            // ホスト名
            hostName = InetAddress.getLocalHost().getHostName();
            
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        
        properties.setProperty("manager.os.name", osName);
        properties.setProperty("manager.os.release", osRelease);
        properties.setProperty("manager.os.version", osVersion);
        properties.setProperty("manager.os.arch", osArch);
        properties.setProperty("manager.os.hostname", hostName);
        properties.setProperty("manager.pid", pid);
    }

    /**
     * <p>ファイルの存在有無を判定します。</p>
     * 
     * @param filePath ファイルパス
     * @return ファイルが存在する場合はtrueを、さもなくばfalseを返します。
     */
    protected boolean fileExist(String filePath) {
        return (new File(filePath)).exists();
    }
    
    /**
     * <p>使用されるコンフィグレーションファイルのパス<p>
     */
    protected String m_configFile;
}

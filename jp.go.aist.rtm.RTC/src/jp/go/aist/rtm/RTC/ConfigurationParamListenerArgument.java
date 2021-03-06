package jp.go.aist.rtm.RTC;

import RTC.ReturnCode_t;
  /**
   * {@.ja ConfigurationParamListenerArgument クラス}
   * {@.en ConfigurationParamListenerArgument class}
   *
   *
   */
public class ConfigurationParamListenerArgument {
    /**
     * {@.ja コンストラクタ}
     * {@.en Constructor}
     *
     */
    public ConfigurationParamListenerArgument(final String config_set_name,
                                              final String config_param_name){
        m_config_set_name = config_set_name; 
        m_config_param_name = config_param_name;
    }
    public String m_config_set_name; 
    public String m_config_param_name;
}




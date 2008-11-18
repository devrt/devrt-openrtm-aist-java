package jp.go.aist.rtm.RTC.util;

import java.util.Vector;

import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;

import RTC.InPortAny;
import RTC.OutPortAny;
import _SDOPackage.NVListHolder;
import _SDOPackage.NameValue;


/**
 * <p>NameValue操作用ユーティリティクラスです。</p>
 */
public class NVUtil {
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するshort値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, short value) {

        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_short(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するlong値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, long value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_longlong(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するfloat値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, float value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_float(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するdouble値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, double value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_double(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するString値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, String value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_wstring(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するInPortAny値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, InPortAny value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_Object(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するOutPortAny値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, OutPortAny value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_Object(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するorg.omg.CORBA.Object値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNV(final String name, org.omg.CORBA.Object value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_Object(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するchar値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNVChar(final String name, final char value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_char(value);
        nv.value = anyValue;
        return nv;
    }

    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するboolean値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNVBool(final String name, final boolean value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_boolean(value);
        nv.value = anyValue;
        return nv;
    }
    
    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するbyte値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNVOctet(final String name, final byte value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_octet(value);
        nv.value = anyValue;
        return nv;
    }

    /**
     * <p>指定されたデータを用いてNameValueオブジェクトを作成します。</p>
     * 
     * @param name オブジェクトに設定する名称
     * @param value オブジェクトに設定するAny値
     * 
     * @return 作成されたNameValueオブジェクト
     */
    public static NameValue newNVAny(final String name, final Any value) {
        
        NameValue nv = new NameValue();
        nv.name = name;
        Any anyValue = ORBUtil.getOrb().create_any();
        anyValue.insert_any(value);
        nv.value = anyValue;
        return nv;
    }

    /**
     * <p>指定されたPropertiesを用いてNameValueオブジェクトリストを作成し、
     * それを指定されたNVListHolder内にコピーします。<br />
     * Propertiesのキーおよび値が、各NameValueの名称および値にそれぞれ割り当てられます。</p>
     * 
     * @param nvlist 作成されたNameValueオブジェクトリストを受け取るためNVListHolder
     * @param prop 設定元となるPropertiesオブジェクト
     */
    public static void copyFromProperties(NVListHolder nvlist, final Properties prop) {
        
        Vector keys = prop.propertyNames();
        int len = keys.size();
        nvlist.value = new NameValue[len];
        for (int intIdx = 0; intIdx < len; ++intIdx) {
            if (nvlist.value[intIdx] == null) {
                nvlist.value[intIdx] = new NameValue();
            }
            nvlist.value[intIdx].name = (String) keys.elementAt(intIdx);
            Any anyValue = ORBUtil.getOrb().create_any();
            anyValue.insert_wstring(prop.getProperty((String) keys.elementAt(intIdx)));
            nvlist.value[intIdx].value = anyValue;
        }
    }
    
    /**
     * <p>指定されたNVListHolderに保持されているNameValueオブジェクトリストの内容を、
     * 指定されたPropertiesオブジェクトにコピーします。<br />
     * 各NameValueの名称および値が、Propertiesのキーおよび値にそれぞれ割り当てられます。</p>
     * 
     * @param prop コピー先のPropertiesオブジェクト
     * @param nvlist コピー元のNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     */
    public static void copyToProperties(Properties prop, final NVListHolder nvlist) {
        
        for (int intIdx = 0; intIdx < nvlist.value.length; ++intIdx) {
            try {
                Any anyVal = nvlist.value[intIdx].value;
                String value = null;
                if( anyVal.type().kind() == TCKind.tk_wstring ) {
                    value = anyVal.extract_wstring();
                } else if( anyVal.type().kind() == TCKind.tk_string ) {
                    value = anyVal.extract_string();
                } else {
                    value = anyVal.extract_Value().toString();
                }
                final String name = new String(nvlist.value[intIdx].name);
                prop.setProperty(name, value);
                
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
    
    /**
     * <p>指定されたNVListHolderに保持されているNameValueオブジェクトリストの内容をもとに、
     * Propertiesオブジェクトを作成します。<br />
     * 各NameValueの名称および値が、Propertiesのキーおよび値にそれぞれ割り当てられます。</p>
     * 
     * @param nvlist 元となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * 
     * @return 作成されたPropertiesオブジェクト
     */
    public static Properties toProperties(final NVListHolder nvlist) {
        to_prop prop = new to_prop();
        prop = (to_prop) CORBA_SeqUtil.for_each(nvlist, prop);
        return prop.m_prop;
    }

    /**
     * <p>NVListHolderが内包するNameValueオブジェクトリストの中から、指定した名称を持つ
     * NameValueオブジェクトを検索して、そのオブジェクトが持つ値をAny型で取得します。</p>
     * 
     * @param nvlist 検索対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * @param name 検索したいNameValueオブジェクトの名称
     * 
     * @return 指定した名称を持つNameValueオブジェクトのAny値
     * 
     * @throws Exception 指定した名称のNameValueオブジェクトが見つからない場合
     */
    public final static Any find(final NVListHolder nvlist, final String name)
            throws Exception {

        int index = CORBA_SeqUtil.find(nvlist, new nv_find(name));
        if (index < 0) {
            throw new Exception("Not found");
        }

        return nvlist.value[index].value;
    }

    /**
     * <p>NVListHolderが内包するNameValueオブジェクトリストの中から、指定した名称を持つ
     * NameValueオブジェクトを検索して、そのオブジェクトのリスト内でのインデクスを取得します。</p>
     * 
     * @param nvlist 検索対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * @param name 検索したいNameValueオブジェクトの名称
     * 
     * @return 指定した名称を持つNameValueオブジェクトが見つかった場合は、
     * そのオブジェクトのリスト内でのインデクスを返します。<br />
     * また、見つからない場合は、-1を返します。
     */
    public final static int find_index(final NVListHolder nvlist, final String name) {
        return CORBA_SeqUtil.find(nvlist, new nv_find(name));
    }
    
    /**
     * <p>NVListHolderが内包するNameValueオブジェクトリストの中から、指定した名称を持つ
     * NameValueオブジェクトを検索して、そのオブジェクトが持つ値が文字列型かどうかを調べます。</p>
     * 
     * @param nvlist 検索対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * @param name 検索したいNameValueオブジェクトの名称
     * 
     * @return 見つかったNameValueオブジェクトの値が文字列型の場合はtrueを、
     * そうでなければfalseを返します。
     */
    public static boolean isString(final NVListHolder nvlist, final String name) {
        try {
            Any value = find(nvlist, name);
            if( value.type().kind() == TCKind.tk_wstring ) {
                value.extract_wstring();
            } else {
                value.extract_string();
            }
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * <p>NVListHolderが内包するNameValueオブジェクトリストの中から、指定した名称を持つ
     * NameValueオブジェクトを検索して、そのオブジェクトが持つ値が文字列型かどうかを調べます。
     * 文字列型の場合は、さらに指定された文字列値に一致するかどうかを調べます。</p>
     * 
     * @param nvlist 検索対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * @param name 検索したいNameValueオブジェクトの名称
     * 
     * @return 見つかったNameValueオブジェクトの値が文字列型で、かつ指定された文字列値に
     * 一致する場合はtrueを返します。また、そうでなければfalseを返します。
     */
    public static boolean isStringValue(final NVListHolder nvlist,
            final String name, final String value) {
        
        if (isString(nvlist, name)) {
            if (toString(nvlist, name).equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>NVListHolderが内包するNameValueオブジェクトリストの中から、指定した名称を持つ
     * NameValueオブジェクトを検索して、そのオブジェクトが持つ値を文字列型で取得します。</p>
     * 
     * @param nvlist 検索対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * @param name 検索したいNameValueオブジェクトの名称
     * 
     * @return 指定した名称を持つNameValueオブジェクトの文字列値を返します。<br />
     * ただし、指定した名称を持つNameValueオブジェクトが見つからない場合や、
     * NameValueオブジェクトの値が文字列型でない場合は、空文字列を返します。
     * 
     * @throws Exception 指定した名称のNameValueオブジェクトが見つからない場合
     */
    public static String toString(final NVListHolder nvlist, final String name) {
        String str_value;
        try {
            Any value = find(nvlist, name);
            if( value.type().kind() == TCKind.tk_wstring ) {
                str_value = value.extract_wstring();
            } else {
                str_value = value.extract_string();
            }
            
        } catch (Exception e) {
            str_value = "";
        }
        
        return str_value;
    }
    
    /**
     * <p>NVListHolderが内包するNameValueオブジェクトリストの中から、指定した名称を持つ
     * NameValueオブジェクトを検索して、そのオブジェクトに指定した文字列値を追加します。<br />
     * 指定した名称を持つNameValueオブジェクトが存在しない場合には、その名称と指定された値を持つ
     * NameValueが新たに作成され、NVListHolder内のNameValueオブジェクトリストに追加されます。</p>
     * 
     * @param nvlist 検索対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     * @param name 検索したいNameValueオブジェクトの名称
     * @param value 見つかったNameValueオブジェクトの値に追加したい文字列値
     */
    public static boolean appendStringValue(NVListHolder nvlist,
            final String name, final String value) {
        
        int index = find_index(nvlist, name);
        boolean find = false;

        if (index > 0) {
            String tmp_char = null;
            if( nvlist.value[index].value.type().kind() == TCKind.tk_wstring ) {
                tmp_char = nvlist.value[index].value.extract_wstring();
            } else {
                tmp_char = nvlist.value[index].value.extract_string();
            }
            String tmp_str = new String(tmp_char);
            String[] values = tmp_str.split(",");
            for (int intIdx = 0; intIdx < values.length; intIdx++) {
                if (values[intIdx].equals(value)) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                tmp_str = tmp_str + "," + value;
                nvlist.value[index].value.insert_wstring(tmp_str);
            }
        } else {
            CORBA_SeqUtil.push_back(nvlist, newNV(name, value));
        }
        
        return true;
    }
    
    /**
     * <p>片方のNameValueListオブジェクトが内包するNameValueオブジェクトリストを、
     * もう一方のNameValueListオブジェクト内に追加します。</p>
     * 
     * @param destNvList 追加先となるNVListHolderオブジェクト
     * @param srcNvList 追加元となるNVListHolderオブジェクト
     */
    public static void append(NVListHolder destNvList, final NVListHolder srcNvList) {
        for (int intIdx = 0; intIdx < srcNvList.value.length; ++intIdx) {
            CORBA_SeqUtil.push_back(destNvList, srcNvList.value[intIdx]);
        }
    }
    
    /**
     * <p>指定されたNVListHolderオブジェクトが内包するNameValueオブジェクトリストの内容を、
     * 標準出力に出力します。</p>
     * 
     * @param nvlist 表示対象となるNameValueオブジェクトリストを内包するNVListHolderオブジェクト
     */
    public static void dump(NVListHolder nvlist) {
        String str_value;
        for (int intIdx = 0; intIdx < nvlist.value.length; ++intIdx) {
            try {
                if( nvlist.value[intIdx].value.type().kind() == TCKind.tk_wstring ) {
                    str_value = nvlist.value[intIdx].value.extract_wstring();
                } else {
                    str_value = nvlist.value[intIdx].value.extract_string();
                }
                System.out.println(nvlist.value[intIdx].name + ":" + str_value);
                
            } catch (Exception ex) {
                System.out.println(nvlist.value[intIdx].name + ": not a string value");
            }
        }
    }

    /**
     * <p>指定された名称を持つNameValueオブジェクトを検索するためのヘルパクラスです。</p>
     */
    static class nv_find implements equalFunctor {

        /**
         * <p>コンストラクタです。検索対象となるNameValueオブジェクトの名称を指定します。</p>
         * 
         * @param name 検索対象となるNameValueオブジェクトの名称
         */
        public nv_find(final String name) {
            m_name = name;
        }

        /**
         * <p>検索対象となる名称を持つNameValueオブジェクトか否かを判定します。</p>
         * 
         * @param nv 判定対象のNameValueオブジェクト
         * 
         * @throws ClassCastException 指定されたオブジェクトがNameValueオブジェクトでない場合
         */
        public boolean equalof(final Object nv) {
            return m_name.equals(((NameValue) nv).name);
        }

        private String m_name;
    }

    /**
     * <p>指定されたNameValueオブジェクトの内容を元にPropertiesオブジェクトを作成する
     * ヘルパクラスです。</p>
     */
    static class to_prop implements operatorFunc {
        
        /**
         * <p>指定されたNameValueオブジェクトの内容を元にPropertiesオブジェクトを作成します。
         * 作成されたPropertiesオブジェクトは、m_propメンバとして取得できます。</p>
         * 
         * @param 作成元となるNameValueオブジェクト
         * 
         * @throws ClassCastException 指定されたオブジェクトがNameValueオブジェクトでない場合
         */
        public void operator(Object elem) {
            operator((NameValue) elem);
        }
        
        /**
         * <p>指定されたNameValueオブジェクトの内容を元にPropertiesオブジェクトを作成します。
         * 作成されたPropertiesオブジェクトは、m_propメンバとして取得できます。</p>
         * 
         * @param 作成元となるNameValueオブジェクト
         */
        public void operator(final NameValue nv) {
            try {
                String value = null;
                if( nv.value.type().kind() == TCKind.tk_wstring || nv.value.type().kind() == TCKind.tk_string ) {
                    if( nv.value.type().kind() == TCKind.tk_wstring ) {
                        value = nv.value.extract_wstring();
                    } else if( nv.value.type().kind() == TCKind.tk_string ) {
                        value = nv.value.extract_string();
                    }
                    m_prop.setProperty(nv.name, value);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        
        public Properties m_prop = new Properties();
    }

}

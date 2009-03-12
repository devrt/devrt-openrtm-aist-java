package RTMExamples.SimpleService;


/**
* RTMExamples/SimpleService/ValueListHolder.java .
* IDL-to-Java �R���p�C�� (�|�[�^�u��), �o�[�W���� "3.1" �Ő���
* ������: src/RTMExamples/MyService.idl
* 2008�N7��17�� 22��25��31�b JST
*/

public final class ValueListHolder implements org.omg.CORBA.portable.Streamable
{
  public float value[] = null;

  public ValueListHolder ()
  {
  }

  public ValueListHolder (float[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = RTMExamples.SimpleService.ValueListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    RTMExamples.SimpleService.ValueListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return RTMExamples.SimpleService.ValueListHelper.type ();
  }

}

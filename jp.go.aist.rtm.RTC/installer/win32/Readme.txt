OpenRTM-aist-Java Windows �C���X�g�[���[�쐬�c�[���̉��

�@�쐬���F 2010/3/29
�@�쐬�ҁF ���c

���@�ڎ�
1. �O�����
2. �f�B���N�g���\��
3. �t�@�C���\��
4. �C���X�g�[������
5. �A���C���X�g�[������


���@����
1. �O�����

�@�@�{�c�[���́AWindows Installer XML(WiX)���g�p���āAmsi�t�@�C����
�@�@�쐬������̂ł��B

�@�@�{�c�[�������s����ɂ́A���L�\�t�g�E�G�A���C���X�g�[������Ă��鎖��
�@�@�O��Ƃ��܂��B

�@�@�@�EPython2.4, 2.5, 2.6 �̉��ꂩ
�@�@�@�EPyYAML-3.09.win32-py2.4, 2.5, 2.6 �̉��ꂩ
�@�@�@�EWiX3.5 Toolset
�@�@�@�E���ϐ��́uPATH�v�ƁuPYTHONPATH�v�ɁA�g�p����Python��񂪐ݒ�
�@�@�@�@����Ă��鎖�B


2. �f�B���N�g��

�@�@���[�J��PC�ɁA�ȉ��̃f�B���N�g���\���ŃC���X�g�[���Ώۃt�@�C����
�@�@�������Ă��鎖��O��Ƃ��Ă��܂��B

�@�@C:\distribution
�@�@�@�@��
�@�@�@�@���� OpenRTM-aist-Java-1.0.0
�@�@�@�@�@�@�@���� Source
�@�@�@�@�@�@�@�@�@�@����examples
�@�@�@�@�@�@�@�@�@�@����jar
�@�@�@�@�@�@�@�@�@�@����JavaDoc
�@�@�@�@�@�@�@�@�@�@����JavaDocEn

�@�@(1) OpenRTM-aist-Java-1.0.0 �́A
�@�@�@�@Java��OpenRTM-aist�̃C���X�g�[���Ώۃt�@�C���ł���A
�@�@�@�@buildRTC �ɂ��쐬���ꂽ�t�@�C�����܂ށB
�@�@�@�@���L�f�B���N�g�����A�f�B���N�g�����R�s�[���ĉ������B
�@�@�@�@�@\jp.go.aist.rtm.RTC\installer\resources\Source

�@�@����L�\���́Amsi�쐬�o�b�` build.cmd �Œ�`���Ă���A�ύX�\�ł��B
�@�@�@�ύX����ꍇ�Abuild.cmd, OpenRTMjavawxs.py �̐���������ĉ������B


3. �t�@�C���\��

�@�@[���̃f�B���N�g��]
�@�@�@�@��
�@�@�@�@���� Bitmaps
�@�@�@�@���@�@���� bannrbmp.bmp �@�C���X�g�[���p�o�i�[�摜�@��1
�@�@�@�@���@�@���� dlgbmp.bmp �@�@�C���X�g�[���p�_�C�A���O�摜�@��1
�@�@�@�@���� License.rtf�@�@�@�@�@���C�Z���X�@��1
�@�@�@�@���� WiLangId.vbs �@�@�@�@���[�e�B���e�B�@��1
�@�@�@�@���� WiSubStg.vbs �@�@�@�@���[�e�B���e�B�@��1
�@�@�@�@���� langs.txt�@�@�@�@�@�@�����Q�[�W�ꗗ�@��1
�@�@�@�@���� makewxs.py �@�@�@�@�@wxsd�t�@�C���W�F�l���[�^�@��1
�@�@�@�@���� uuid.py�@�@�@�@�@�@�@UUID�W�F�l���[�^�@��1
�@�@�@�@���� yat.py �@�@�@�@�@�@�@Yaml�e���v���[�g�v���Z�b�T�@��1
�@�@�@�@��
�@�@�@�@��
�@�@�@�@���� License.txt�@�@���C�Z���X
�@�@�@�@���� build.cmd�@�@�@msi�쐬�o�b�`
�@�@�@�@���� cleanup.cmd�@�@�e���|�����t�@�C���폜�o�b�`
�@�@�@�@��
�@�@�@�@���� OpenRTM-aist-Java.wxs.in�@OpenRTM-aist�pWiX�e���v���[�g
�@�@�@�@���� WixUI_Mondo_RTM.wxs �@�@�@OpenRTM�p���[�U�[�E�C���^�t�F�C�X
�@�@�@�@��
�@�@�@�@���� OpenRTMjavawxs.py�@�@�@OpenRTM-aist�pwxs�t�@�C���W�F�l���[�^
�@�@�@�@��
�@�@�@�@���� WixUI_**-**.wxl�@�@�@�e���ꃁ�b�Z�[�W���[�J���C�Y�@��2


�@�@�@��1�́AC++�ł��R�s�[�������́B

�@�@�@��2�́AWixUI_Mondo_RTM.wxs �p�ɁA�C���X�g�[���̎�ނ�I�������ʂ�
�@�@�@�@�\�����郁�b�Z�[�W���A�I���W�i���̃��[�J���C�Y�t�@�C�������ɁA
�@�@�@�@InstallScopeDlgDescription �̃��b�Z�[�W��
�@�@�@�@SetupTypeDlgDescription ���R�s�[���Ă��܂��B


�@�@[�r���h��Ɏg�p����t�@�C��]
�@�@�@�@��
�@�@�@�@���� OpenRTM-aist-Java-1.0.0.msi�@�p��̃C���X�g�[���[
�@�@�@�@��
�@�@�@�@���� OpenRTM-aist-Java-1.0.0_**-**.msi�@�e���ꖈ�̃C���X�g�[���[

�@�@�@��build.cmd �����s����ƁA�����̃e���|�����t�@�C����msi�t�@�C����
�@�@�@�@�쐬����܂��B
�@�@�@�@�C�ӂ�msi�t�@�C�������g�p�������B


4. �C���X�g�[������

�@(1) �C���X�g�[�����s���ꍇ�A�C���X�g�[����PC�̃��W�X�g�����`�F�b�N���A
�@�@�@JDK1.5, 1.6 �̉��ꂩ���o�^�ς݂ł��邩���肵�܂��B
�@�@�@������o�^����Ă��Ȃ���΁A�G���[���b�Z�[�W��\�����āA
�@�@�@�C���X�g�[�����I�����܂��B

�@(2) JDK1.5, 1.6 �̉��ꂩ���o�^�ς݂ł���ꍇ
�@�@�EProgram Files �t�H���_�� �����^�C���t�@�C���A�N���X���t�@�����X
�@�@�@�t�@�C���Aexamples�t�@�C�� ���C���X�g�[�����܂��B

�@(3) �X�^�[�g�{�^���̃v���O�������j���[��
�@�@�@�EOpenRTM-aist -> Java -> documents ���
�@�@�@�@���{��Ɖp��̃N���X���t�@�����X��I���o���܂��B
�@�@�@�EOpenRTM-aist -> Java -> examples ���
�@�@�@�@�uStart orbd�v�Ɗeexample ��I���o���܂��B


5. �A���C���X�g�[������

�@(1) �A���C���X�g�[�����s���ꍇ�A�C���X�g�[���t�@�C���Ƃ͕ʂɁA
�@�@�@Program Files\OpenRTM-aist\1.0\examples\Java �f�B���N�g�� ��
�@�@�@���O�t�@�C��(*.log)���폜���܂��B

�ȏ�

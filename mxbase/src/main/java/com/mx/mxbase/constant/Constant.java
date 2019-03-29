package com.mx.mxbase.constant;

/**
 * class of constant
 * Created by Archer on 16/7/29.
 */
public class Constant {
    //需要显示的应用列表
    public static String[] appName = {"计算器", "浏览器", "录音机", "设置", "时钟", "搜索", "图库",
            "下载内容", "音乐", "字典工具"};
    //每次打包的时候需要设置当前主界面的包名
    public static String MAIN_PACKAGE = "com.mx.zhude";
    //注册界面接收验证码区分哪个端
    public static String CODE_CLIENT = "c";//b,c  b端不需要当当key
    //是否是海尔端用于接收验证码前缀
    public static String CLENT_APP = "haier";//haier  moxi
    //本地虚拟底层版本号
    public static int LOCAL_VIR_VERSION = 2;
    //网络请求HOST
    public static String HTTP_HOST = "http://120.25.193.163";
    //第一次登录madmadnand
    public static String HTTP_FIRST = "http://120.25.193.163:8888";
    //网络请求备用HOST
//    public static String HTTP_HOST = "http://10.79.10.100:8088";
    //用户注册
//    public static String USER_REGISTER = HTTP_HOST + "/app/member/reg";//用户注册

    public static final String HISTORYURL = HTTP_HOST + "/app/exerciseResult/historys";
    //历史纪录
    public static final String HISTORDISS = HTTP_HOST + "/app/coursePaper/queryPaperOrderByRecommend";

    public static final String GETCODE = HTTP_HOST + "/app/appversion/getLatestVersion";
    //获取书籍类型列表
    public static String GET_BOOK_TYPE_LIST = HTTP_HOST + "/app/booktype/list";
    //电子书关机与待机图片信息
    public static String GET_POWER_AND_STOP_PHOTO = HTTP_HOST + "/app/adSite/getAdImageHal";
    //获取书籍列表
    public static String GET_BOOK_LIST = HTTP_HOST + "/app/book/list";
    //获取书籍详情
    public static String GET_BOOK_DETAILS = HTTP_HOST + "/app/book/get/";//后面跟书籍id
    //根据书籍id下载书籍
    public static String DOWNLOAD_BOOK_BY_ID = HTTP_HOST + "/app/book/download/";//后面跟书籍id
    //书籍购买测试二维码地址
    public static String BOOK_PAY_QRCODE = HTTP_HOST + "/app/bookbuytest/qrcode/";//后面跟书籍id
    //用户注册
    public static String USER_REGISTER = HTTP_HOST + "/app/member/V3/reg";//用户注册  /reg
    //用户登录(普通端登录)
//    public static String USER_LOGIN = HTTP_HOST + "/app/member/login";//用户登录
    //用户登录（需要当当书城端登录）loginWithDangdang
    public static String USER_LOGIN = HTTP_HOST + "/app/member/V2/loginWithDangdang";//用户登录
    //用户登录整合当当用户绑定
    public static String USER_LOGIN_V3 = HTTP_HOST + "/app/member/V3/loginWithDangdang";
    //获取当当图形验证码
    public static String GET_DD_PIC_VERIFICATION = HTTP_HOST + "/app/member/V3/loginValidate";
    //adadadn
    public static String B_LOGIN = HTTP_HOST + "/app/member/loginStudentB";
    //用户登录  调用第一次登录
    public static String USER_FIRST_LOGIN = HTTP_HOST + "/app/user/login";
    //登录类型 只能为  1   和   0
    public static String LOGIN_TYPE = "1";
    //获取验证码 /app/member/sendCode
    public static String GET_USER_CODE = HTTP_HOST + "/app/member/sendCode";
    //找回密码验证验证码
    public static String RETRIEVE_CODE = HTTP_HOST + "/app/member/retrieveValidate";
    //用户更改密码
    public static String MODIFAY_PASSWORD = HTTP_HOST + "/app/member/retrievePassword";
    //用户更改密码2
    public static String UPDATE_PASSWORD = HTTP_HOST + "/app/memberInfo/updatePassword";
    //获取用户基本信息
    public static String GET_USER_INFO = HTTP_HOST + "/app/memberInfo/getMemberInfo";
    //更新用户基本信息
    public static String UPDATE_USER_INFO = HTTP_HOST + "/app/memberInfo/modifyMember";
    //更改头像
    public static String MODIFY_USER_AVATAR = HTTP_HOST + "/app/memberInfo/modifyHeadPortrait";
    //下载更新包
    public static String DOWNLOAD_UPDATE_ZIP = HTTP_HOST + "/app/appversion/download/";
    //获取同步练习详细数据接口
    public static String GET_SYNC_EXAMS = HTTP_HOST + "/app/exercise/exerciseListByChapterId?courseChapterId=";
    //下载试卷接口
    public static String DOWN_EXAMS_NATIVE = HTTP_HOST + "/app/coursePaper/";
    //提交试卷做题结果
    public static String SUBMIT_EXAMS_RESULT = HTTP_HOST + "/app/coursePaper/savePaperAnswer";
    //提交同步练习结果
    public static String SUBMIT_TB_RESULT = HTTP_HOST + "/app/exerciseResult/saveExerciseResult";
    //书本更新
    public static String SUBMIT_DB_UPDATE = HTTP_HOST + "/app/exercise/checkUpdate";
    //检测底层版本是否有更新
    public static String CHECK_LOWER_UPDATE = HTTP_HOST + "/app/appversion/getBaseCode";
    //检测apk版本是否有更新
    public static String CHECK_VERSION = HTTP_HOST +"/app/appversion/checkNew";
    //删除做题历史记录
    public static String DEL_HISTORY_EXAMS = HTTP_HOST + "/app/exerciseResult/removeHistory";
    //查询某个章节下的历史记录
    public static String QUERY_SYNC_HISTORY = HTTP_HOST + "/app/exerciseResult/getHistoryRecord";
    //是否打印日志
    public static boolean isPrint = false;
    //设置网络超时时间
    public static int CONNECT_TIMEOUT = 30000;
    //网络请求成功
    public static String SUCCESS = "0";
    //资源存储路径
    public static String FILE_PATH = "/Books/";
    //试卷存储路径
    public static String EXAMS_PATH = "/Exams";
    public static String USER_INFO = "mx_user_info";
    //再来五题编号
    public static int EXAMS_ONE_MORE_TIME = -1;
    //获取广告位图片
    public static String GET_ADVSING = HTTP_HOST + "/app/adSite/getAdImage/1";
    //获取广告位图片
    public static String GET_ADVSING2 = HTTP_HOST + "/app/adSite/getAdImage/2";
    //获取广告位图片
    public static String GET_ADVSING2_JSON = HTTP_HOST + "/app/adSite/getAdImage/2/json";
    //当当用户绑定接口
    public static String bindDDUser = HTTP_HOST + "/app/member/bindingDangdang";
    //当当用户解绑接口
    public static String unBindUrl = HTTP_HOST + "/app/member/unBindingDangdang";
    public static String registerCodeUrl = HTTP_HOST + "/app/member/sendSMSWithRegisterDangdang";
    public static String registerDDUser = HTTP_HOST + "/app/member/registerDangdang";
    public static String verificationUrl = HTTP_HOST + "/app/member/V3/loginValidate";
    public static String uploadDevicePsw = HTTP_HOST + "/app/member/ininData";
}

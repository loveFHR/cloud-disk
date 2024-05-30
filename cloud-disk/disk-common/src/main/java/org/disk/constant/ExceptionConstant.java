package org.disk.constant;

public class ExceptionConstant {

    public static final String USER_EXIST_EXCEPTION_MSG = "该用户名或邮箱已被注册，请更换用户名或邮箱";
    public static final String UNKNOWN_EXCEPTION = "系统错误，请稍后重试";
    public static final String NO_REGISTER_EMAIL = "该邮箱尚未注册，请先注册";
    public static final String USER_PASSWORD_ERROR = "密码错误";
    public static final String CODE_ERROR = "验证码错误，请重新输入!";
    public static final String CODE_LAPSE = "该验证码已不存在或已过期，请重新发送!";
    public static final String MAIL_NOT_EXIST = "该邮箱不存在，请输入正确的邮箱!";
    public static final String FILE_NOT_EXIST = "文件不存在";
    public static final String UPLOAD_FILE_INTO_MINIO_FAILED = "上传文件失败";
    public static final String PASSWORD_REGEX_ERROR = "请输入6-16位的密码！";
    public static final String MAIL_ERROR = "请输入正确的邮箱！";
    public static final String USER_NAME_ERROR = "请输入4-16位的用户名(字母、数字、下划线)";
    public static final String PHONE_ERROR = "请输入正确的手机号";
    public static final String SAVE_FILE_INFORMATION_FAILED = "保存文件信息失败";
    public static final String SAVE_USER_FILE_INFORMATION_FAILED = "保存用户文件关系失败";
    public static final String UPLOAD_CHUNK_FILE_FAILED = "上传分块文件失败";
    public static final String MERGE_FILE_FAILED = "合并文件失败";
    public static final String DOWNLOAD_FILE_SUCCESS = "下载成功";
    public static final String DOWNLOAD_FILE_FAILED = "下载失败";
    public static final String LACK_SPACE = "空间不足";
    public static final String UPLOAD_SUCCESS = "上传成功";
    public static final String UPDATE_FILE_FAILED = "更新文件信息失败";
    public static final String UPDATE_CPA_FAILED = "更新容量失败";
    public static final String UPLOAD_AVATAR_FAILED = "文件格式错误";
    public static final String CLEARING_FAILED = "回收站中无文件！";


    public static final String NULL_ERROR = "请求参数为空";
    public static final String PARAM_ERROR = "请求参数错误";
    public static final String NO_AUTH = "无权限";
    public static final String USER_STATUS_ERROR = "该用户已被禁用，请联系管理员解禁";
}

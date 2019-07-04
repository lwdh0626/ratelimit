package com.july;

import com.july.interceptor.Log;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class LogUtils {
    //工具类中的service注入不能用注解方式   所以用spring方式载入
//    private static LogService logService = SpringContextHolder.getBean("logService");
    /**
     * 保存日志
     * @throws UnsupportedEncodingException
     */
    public static void saveLog(HttpServletRequest request, Object handler, Exception ex, String title) throws UnsupportedEncodingException {
//        User user = UserUtils.getSession(User.SESSION_KEY);
//        if (user != null && user.getId() != null){
            Log log = new Log();
            log.setTitle(title);
//            log.setType(ex == null ? Log.TYPE_ACCESS : Log.TYPE_EXCEPTION);// 类型（1：接入日志；2：错误日志）
            log.setTimer(Long.parseLong(request.getAttribute("timer")+""));// 耗时
//            log.setRemoteaddr(StringUtils.getRemoteAddr(request));// 操作用户的IP地址
//            log.setUseragent(request.getHeader("user-agent"));// 操作用户代理信息
//            log.setRequesturi(request.getRequestURI());// 操作的URI
//            log.setParams(request.getParameterMap());// 操作提交的数据
//            log.setMethod(request.getMethod()); // 操作的方式
//            UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
//            String browser =userAgent.getBrowser().getName();//获取浏览器名称
//            String windows =userAgent.getOperatingSystem().getName();//获取操作系统
//            log.setBrowsername(browser);// 浏览器名称
//            log.setWindows(windows);//操作系统名称
//            log.setCity(IpAddress.getAddresses("ip="+StringUtils.getRemoteAddr(request), "utf-8"));//ip所在市
//            User u = UserUtils.getSession(User.SESSION_KEY);
//            if (u != null) {
//                log.setCreateby(u.getId().toString());
//                log.setUpdateby(u.getId().toString());
//            }
//            log.setCreatedate(System.currentTimeMillis());
//            log.setUpdatedate(System.currentTimeMillis());
            // 异步保存日志
            new SaveLogThread(log, handler, ex).start();
        }
    }


    /**
     * 开启保存日志线程
     */
     class SaveLogThread extends Thread{

        private Log log;
        private Object handler;
        private Exception ex;

        public SaveLogThread(Log log, Object handler, Exception ex){
            super(SaveLogThread.class.getSimpleName());
            this.log = log;
            this.handler = handler;
            this.ex = ex;
        }

        @Override
        public void run() {
            // 获取日志标题
//            if (StringUtils.isBlank(log.getTitle())){
//                String title = null ;
//                if (handler instanceof HandlerMethod){
//                    Method m = ((HandlerMethod)handler).getMethod();
//                    Logger clazz = m.getDeclaringClass().getAnnotation(Logger.class);//类注解
//                    Logger method = m.getAnnotation(Logger.class);//方法注解
//
//                    if(clazz != null && method != null) {
//                        String[] s = new String[2];
//                        s[0] = clazz.name();
//                        s[1] = method.name();
//                        title = StringUtils.join(s, "-");
//                    }else {
//                        title = "";
//                    }
//                }
//                log.setTitle(title);
//            }
//            // 如果有异常，设置异常信息
//            log.setException(Exceptions.getStackTraceAsString(ex));
//            // 如果无标题并无异常日志，则不保存信息
//            if (StringUtils.isBlank(log.getTitle()) && StringUtils.isBlank(log.getException())){
//                return;
//            }
            // 保存日志信息
//            logService.save(log);
            System.out.println("EXCE-----"+getErrorMessage(ex));
            System.out.println(log);
        }


        public static String getErrorMessage(Exception e){
            if(null == e){
                return "SUCESS";
            }
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return  sw.toString();
        }

    }


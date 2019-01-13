package com.example.laiyang.nest.threadPool;

/**  --------------------------------------------------------工厂模式 --------------------------------------------------------------
 * Created by laiyang on 2018/11/3 20:13
 * 这是一个代理对象，面向对象编程思维，代理比继承灵活很多！
 * 可以在这里写其他代理对象；
 */
public class ThreadPoolProxyFactory {

    private static ThreadPoolProxy mNormalThreadPoolProxy;

    /**
     * 得到普通线程池代理对象mNormalThreadPoolProxy
     */

    public static ThreadPoolProxy getNormalThreadPoolProxy(){
        if (mNormalThreadPoolProxy == null){
            synchronized (ThreadPoolProxyFactory.class){
                if (mNormalThreadPoolProxy == null){
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5,10);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

}

package top.duwd.common.service.proxy;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.domain.proxy.ProxyEntity;
import top.duwd.common.mapper.proxy.ProxyMapper;
import top.duwd.dutil.date.DateUtil;
import top.duwd.dutil.proxy.ProxyUtil;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProxyService {
    @Autowired
    private ProxyMapper proxyMapper;

    /**
     * 从DB 先获取IP， 如果没有 则取Web获取
     * @param minute
     * @return
     */
    public Proxy getProxy(int minute) {
        Date date = new Date();
        Example example = new Example(ProxyEntity.class);
        example.createCriteria().andBetween("createTime", DateUtil.addMin(date, -minute), date);
        example.setOrderByClause("id desc");

        List<ProxyEntity> list = proxyMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(list.get(0).getIp(), Integer.parseInt(list.get(0).getPort())));
        } else {
            return getProxyAndSaveDB();
        }
    }

    /**
     * 从DB 先获取IP， 如果没有 则取Web获取
     * @param minute
     * @return
     */
    public Proxy getProxy(int minute,int mod) {
        Date date = new Date();
        Example example = new Example(ProxyEntity.class);
        example.createCriteria()
                .andEqualTo("flag",mod)
                .andBetween("createTime", DateUtil.addMin(date, -minute), date);
        example.setOrderByClause("id desc");

        RowBounds r1 = new RowBounds(0, 1);
        List<ProxyEntity> list = proxyMapper.selectByExampleAndRowBounds(example,r1);

        if (list != null && list.size() > 0) {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(list.get(0).getIp(), Integer.parseInt(list.get(0).getPort())));
        } else {
            return getProxyAndSaveDB(mod);
        }
    }

    @Nullable
    public Proxy getProxyAndSaveDB() {
        //从网络获取最新IP
        JSONObject moguProxy = ProxyUtil.getMoguProxy();
        if (moguProxy != null) {
            ProxyEntity proxyEntity = new ProxyEntity();
            String ip = moguProxy.getString("ip");
            String port = moguProxy.getString("port");
            proxyEntity.setPlat("moguproxy.com");
            proxyEntity.setIp(ip);
            proxyEntity.setPort(port);
            proxyEntity.setCreateTime(new Date());
            proxyMapper.insert(proxyEntity);
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port)));
        } else {
            return null;
        }
    }
    @Nullable
    public Proxy getProxyAndSaveDB(int flag) {
        //从网络获取最新IP
        JSONObject moguProxy = ProxyUtil.getMoguProxy();
        if (moguProxy != null) {
            ProxyEntity proxyEntity = new ProxyEntity();
            String ip = moguProxy.getString("ip");
            String port = moguProxy.getString("port");
            proxyEntity.setPlat("moguproxy.com");
            proxyEntity.setIp(ip);
            proxyEntity.setPort(port);
            proxyEntity.setCreateTime(new Date());
            proxyEntity.setFlag(flag);
            proxyMapper.insert(proxyEntity);
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port)));
        } else {
            return null;
        }
    }

}

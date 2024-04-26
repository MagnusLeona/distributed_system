package magnus.leona.seata.aaa.service;

import io.seata.spring.annotation.GlobalTransactional;
import magnus.leona.seata.aaa.mapper.aaa.AAAMapper;
import magnus.leona.seata.aaa.mapper.bbb.BBBMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    @Autowired
    private SqlSessionFactory aaaSqlSessionFactory;

    @Autowired
    private SqlSessionFactory bbbSqlSessionFactory;

    @GlobalTransactional
    public void transfer() {
        AAAMapper aaaMapper = aaaSqlSessionFactory.openSession(true).getMapper(AAAMapper.class);
        BBBMapper bbbMapper = bbbSqlSessionFactory.openSession(true).getMapper(BBBMapper.class);
        aaaMapper.updateAccount(100);
        int i = 10 / 0;
        bbbMapper.updateAccount(100);
    }
}

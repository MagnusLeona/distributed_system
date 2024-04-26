package magnus.leona.seata.aaa.mapper.bbb;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BBBMapper {

    @Update("update user set money=money+#{money} where id=1")
    void updateAccount(int money);
}


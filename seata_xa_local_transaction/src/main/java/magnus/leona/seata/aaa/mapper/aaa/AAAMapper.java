package magnus.leona.seata.aaa.mapper.aaa;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AAAMapper {

    @Update("update user set money=money-#{money} where id=1")
    void updateAccount(int money);
}

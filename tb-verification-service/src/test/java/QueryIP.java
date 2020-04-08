import com.alibaba.fastjson.JSON;
import com.hwj.tieba.TBVerificationService;
import com.hwj.tieba.util.QueryIPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TBVerificationService.class)
public class QueryIP {
    @Autowired
    public QueryIPUtil queryIPUtil;

    @Test
    public void queryIP(){
       String data = queryIPUtil.qureyIP(QueryIPUtil.KEY,"120.243.244.245", QueryIPUtil.OUTPUT_TYPE.JSON);
       System.out.print(data);
    }
}

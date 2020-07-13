import com.hwj.tieba.TBMailService;
import com.hwj.tieba.util.ApiCallUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TBMailService.class)
public class QueryIP {
    @Autowired
    public ApiCallUtil queryIPUtil;

    @Test
    public void queryIP(){
       String data = queryIPUtil.qureyIP(ApiCallUtil.KEY,"120.243.244.245", ApiCallUtil.OUTPUT_TYPE.JSON);
       System.out.print(data);
    }
}

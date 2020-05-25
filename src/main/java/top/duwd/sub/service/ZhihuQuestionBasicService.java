package top.duwd.sub.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import top.duwd.common.domain.sub.entity.KeywordBaiduSearchResult;
import top.duwd.common.domain.sub.entity.ZhihuQuestionBasic;
import top.duwd.common.mapper.sub.KeywordBaiduSearchResultMapper;
import top.duwd.common.mapper.sub.ZhihuQuestionBasicMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ZhihuQuestionBasicService {

    @Autowired
    private ZhihuQuestionBasicMapper mapper;
    @Autowired
    private KeywordBaiduSearchResultMapper keywordBaiduSearchResultMapper;

    public static final String prefix = "zhihu.com/question/";

    //对 百度结果 知乎 去重
    public int deleteRepeat() {
        List<Integer> minIdList = keywordBaiduSearchResultMapper.findRepeatIdListZhihu();
        List<Integer> saveIdListAll = keywordBaiduSearchResultMapper.findRepeatIdListAllZhihu();
        List<Integer> deleteAll = new ArrayList<>();
        if (saveIdListAll != null && saveIdListAll.size() > 0) {

            for (Integer i : saveIdListAll) {
                if (!minIdList.contains(i)) {
                    deleteAll.add(i);
                }
            }

        }

        if (deleteAll.size() > 0) {
            log.info("删除重复数据 id list [{}]", JSON.toJSONString(deleteAll));
            Example example = new Example(KeywordBaiduSearchResult.class);
            example.createCriteria().andIn("id", deleteAll);
            return keywordBaiduSearchResultMapper.deleteByExample(example);
        } else {
            return 0;
        }
    }

    /**
     * 获取为处理的 百度 结果  知乎question
     * @return
     */
    public List<KeywordBaiduSearchResult> findListZhihuQuestion() {
        List<KeywordBaiduSearchResult> list = keywordBaiduSearchResultMapper.findListZhihuQuestionZhihu("%" + prefix + "%", 100);
        List<KeywordBaiduSearchResult> results = new ArrayList<>();
        List<Integer> qids = new ArrayList<>();


        if (list != null && list.size() > 0) {
            for (KeywordBaiduSearchResult url : list) {
                int qid = findQid(url.getUrlReal(), prefix);
                if (qid != 0 && qids.indexOf(qid) < 0) {
                    qids.add(qid);
                    results.add(url);
                }
            }
        }
        if (results.size() > 0) {
            return results;
        }
        return null;
    }


    public static void main(String[] args) {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("http://zhihu.com/question/45035312/answer/1181510106");
        strings.add("http://zhihu.com/question/45035312/answer/1181510106");
        strings.add("https://www.zhihu.com/question/270098966");
        strings.add("https://www.zhihu.com/question/341012168/answer/794589458");
        strings.add("https://www.zhihu.com/question/50165063");
        strings.add("https://www.zhihu.com/question/50165063");
        strings.add("https://www.zhihu.com/question/364176152");
        strings.add("https://www.zhihu.com/question/364176152");
        strings.add("https://www.zhihu.com/question/52240114");
        strings.add("https://www.zhihu.com/question/287657635");
        strings.add("https://www.zhihu.com/question/21009978");
        strings.add("https://www.zhihu.com/question/21009978");
        strings.add("https://www.zhihu.com/question/25338860");
        strings.add("https://www.zhihu.com/question/21324495?sort=created");
        strings.add("https://www.zhihu.com/question/353163620/answer/875384348");
        strings.add("https://zhuanlan.zhihu.com/p/48781714");
        strings.add("https://zhuanlan.zhihu.com/p/87142500");
        strings.add("https://www.zhihu.com/question/320752290");
        strings.add("https://www.zhihu.com/question/320752290");
        strings.add("https://zhuanlan.zhihu.com/p/89260253");
        strings.add("https://www.zhihu.com/question/22943775/answer/423202197");
        strings.add("https://www.zhihu.com/question/22943775");
        strings.add("https://www.zhihu.com/question/320526236");
        strings.add("https://www.zhihu.com/question/377013470/answer/1063592597");
        strings.add("https://www.zhihu.com/question/377013470/answer/1063592597");
        strings.add("https://www.zhihu.com/question/291078417");
        strings.add("https://zhuanlan.zhihu.com/p/100865750");
        strings.add("https://www.zhihu.com/question/37148747");
        strings.add("https://zhuanlan.zhihu.com/p/80431737");
        strings.add("https://zhuanlan.zhihu.com/p/80431737");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://www.zhihu.com/question/300718821");
        strings.add("https://www.zhihu.com/question/21464697");
        strings.add("https://www.zhihu.com/question/389839407");
        strings.add("https://www.zhihu.com/question/352423195");
        strings.add("https://www.zhihu.com/question/301267187/answer/735129728");
        strings.add("https://www.zhihu.com/question/37432037/answer/135355497");
        strings.add("https://www.zhihu.com/question/389839407/answer/1176233406");
        strings.add("https://www.zhihu.com/question/311632047");
        strings.add("https://zhuanlan.zhihu.com/p/71281946");
        strings.add("https://zhuanlan.zhihu.com/p/44197386");
        strings.add("https://www.zhihu.com/question/26965052");
        strings.add("https://www.zhihu.com/question/47107217");
        strings.add("https://www.zhihu.com/question/290703077");
        strings.add("https://www.zhihu.com/question/290703077");
        strings.add("https://www.zhihu.com/question/356992602");
        strings.add("https://www.zhihu.com/question/297277921/answer/512685885");
        strings.add("https://www.zhihu.com/question/356992602");
        strings.add("https://zhuanlan.zhihu.com/p/98654204");
        strings.add("https://zhuanlan.zhihu.com/p/133983860");
        strings.add("https://www.zhihu.com/question/387290556");
        strings.add("https://www.zhihu.com/question/387290556/answer/1152107826");
        strings.add("https://www.zhihu.com/question/387290556/answer/1149698634");
        strings.add("https://www.zhihu.com/question/387290556");
        strings.add("https://www.zhihu.com/question/387290556/answer/1152107826");
        strings.add("https://zhuanlan.zhihu.com/p/101539575");
        strings.add("https://www.zhihu.com/question/26544246");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("http://zhihu.com/question/22958427/answer/1215165905");
        strings.add("https://www.zhihu.com/question/26486963");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://zhuanlan.zhihu.com/p/29402677");
        strings.add("https://www.zhihu.com/question/332264718/answer/790987012");
        strings.add("https://www.zhihu.com/question/39635424");
        strings.add("https://zhuanlan.zhihu.com/p/103771913");
        strings.add("https://www.zhihu.com/question/29690145");
        strings.add("https://www.zhihu.com/question/50260049/answer/130627876");
        strings.add("https://www.zhihu.com/question/31566235");
        strings.add("https://www.zhihu.com/question/31566235");
        strings.add("https://www.zhihu.com/question/332264718");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://www.zhihu.com/question/375244848");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://www.zhihu.com/question/300718821/answer/522170489");
        strings.add("https://www.zhihu.com/question/29666661?sort=created");
        strings.add("https://www.zhihu.com/question/21223237/answer/29145646");
        strings.add("https://www.zhihu.com/question/360297436");
        strings.add("https://www.zhihu.com/question/277117387");
        strings.add("https://www.zhihu.com/question/21058886");
        strings.add("https://www.zhihu.com/question/26486963?sort=created");
        strings.add("https://zhuanlan.zhihu.com/p/101539575");
        strings.add("https://www.zhihu.com/question/57926438/answer/1190511233");
        strings.add("https://www.zhihu.com/question/21509495/answer/805145055");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/58360904/answer/814004176");
        strings.add("https://www.zhihu.com/question/382285620/answer/1115256306");
        strings.add("https://www.zhihu.com/question/29392422");
        strings.add("https://www.zhihu.com/question/311058149");
        strings.add("https://zhuanlan.zhihu.com/p/51808858");
        strings.add("https://zhuanlan.zhihu.com/p/98393623");
        strings.add("https://www.zhihu.com/question/21324495");
        strings.add("https://www.zhihu.com/question/41784534");
        strings.add("https://www.zhihu.com/question/341012168/answer/793555334");
        strings.add("https://www.zhihu.com/question/48759272/answer/654353796");
        strings.add("https://www.zhihu.com/question/48759272/answer/654353796");
        strings.add("https://www.zhihu.com/question/20070847");
        strings.add("https://www.zhihu.com/question/23500766?sort=created");
        strings.add("https://www.zhihu.com/question/25252854?sort=created");
        strings.add("https://www.zhihu.com/question/25252854?sort=created");
        strings.add("https://zhuanlan.zhihu.com/p/62945164");
        strings.add("https://zhuanlan.zhihu.com/p/41918537");
        strings.add("https://www.zhihu.com/question/323171934/answer/1086776961");
        strings.add("https://www.zhihu.com/question/39869932/answer/695177831");
        strings.add("https://www.zhihu.com/question/339526348/answer/797971340");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376/answer/1145620148");
        strings.add("https://www.zhihu.com/question/21023376/answer/82842081");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/21023376");
        strings.add("https://www.zhihu.com/question/292552240");
        strings.add("https://www.zhihu.com/question/57926438");
        strings.add("https://www.zhihu.com/question/22958427/answer/1215165905");
        strings.add("https://www.zhihu.com/question/22958427");
        strings.add("https://zhuanlan.zhihu.com/p/57186233");
        strings.add("https://www.zhihu.com/question/337695243/answer/769756328");
        strings.add("https://www.zhihu.com/question/316135544");
        strings.add("https://www.zhihu.com/question/356699453");
        strings.add("https://zhuanlan.zhihu.com/p/67830450");
        strings.add("https://www.zhihu.com/question/27137036/answer/35435950");
        strings.add("https://zhuanlan.zhihu.com/p/32876001");
        strings.add("https://www.zhihu.com/topic/19829014/hot");
        strings.add("https://zhuanlan.zhihu.com/p/32876001?utm_source=wechat_session");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/21509495");
        strings.add("https://www.zhihu.com/question/304739005");
        strings.add("https://www.zhihu.com/question/22092256/answer/1091613036");
        strings.add("https://www.zhihu.com/question/22092256");
        strings.add("https://www.zhihu.com/question/367180299");
        strings.add("https://www.zhihu.com/question/335149560");
        strings.add("https://www.zhihu.com/question/335149560");
        strings.add("https://www.zhihu.com/question/22092256/answer/811003935");
        strings.add("https://www.zhihu.com/question/22092256/answer/811003935");
        strings.add("https://www.zhihu.com/question/31910298");
        strings.add("https://www.zhihu.com/question/55629047");
        strings.add("https://zhuanlan.zhihu.com/p/49421456");
        strings.add("https://zhuanlan.zhihu.com/p/49421456");
        strings.add("https://www.zhihu.com/question/55629047");
        strings.add("https://zhuanlan.zhihu.com/p/49421456");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://www.zhihu.com/question/20269949/answer/660836029");
        strings.add("https://www.zhihu.com/question/20269949/answer/660836029");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://www.zhihu.com/question/23929577");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("http://zhihu.com/question/28953140/answer/1220431704");
        strings.add("https://www.zhihu.com/question/46923879");
        strings.add("https://www.zhihu.com/question/39071195/answer/347824306");
        strings.add("https://www.zhihu.com/question/39071195");
        strings.add("https://www.zhihu.com/question/39071195");
        strings.add("https://www.zhihu.com/question/353163620/answer/875384348");
        strings.add("https://www.zhihu.com/question/58360904/answer/814004176");
        strings.add("https://www.zhihu.com/question/21509495?sort=created");
        strings.add("https://www.zhihu.com/question/58360904/answer/814004176");
        strings.add("https://www.zhihu.com/question/296525486");
        strings.add("https://www.zhihu.com/question/311058149");
        strings.add("https://www.zhihu.com/question/311058149");
        strings.add("https://zhuanlan.zhihu.com/p/32090635");
        strings.add("https://www.zhihu.com/question/340923635");
        strings.add("https://www.zhihu.com/question/340923635/answers/updated");
        strings.add("https://www.zhihu.com/question/276398123");
        strings.add("https://www.zhihu.com/question/382285620");
        strings.add("https://www.zhihu.com/question/382955597");
        strings.add("https://www.zhihu.com/question/382955597");
        strings.add("https://www.zhihu.com/question/20842469");
        strings.add("https://www.zhihu.com/question/20842469?sort=created");
        strings.add("https://www.zhihu.com/question/20842469?sort=created");
        strings.add("https://www.zhihu.com/question/20842469?sort=created");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("https://www.zhihu.com/question/266333633");
        strings.add("https://www.zhihu.com/question/341223076/answer/1096566395");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("https://www.zhihu.com/question/341223076/answer/1096566395");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("https://www.zhihu.com/question/330784999?utm_source=wechat_timeline");
        strings.add("https://www.zhihu.com/question/20842469");
        strings.add("https://zhuanlan.zhihu.com/p/75041205");
        strings.add("https://www.zhihu.com/question/23413050");
        strings.add("https://www.zhihu.com/question/23413050/answer/1235498012");
        strings.add("https://www.zhihu.com/question/23413050");
        strings.add("https://www.zhihu.com/question/310485852");
        strings.add("https://www.zhihu.com/question/353284078");
        strings.add("https://www.zhihu.com/question/304739005");
        strings.add("https://www.zhihu.com/question/364513281/answer/962729713");
        strings.add("http://zhihu.com/question/65282075/answer/1235600732");
        strings.add("https://www.zhihu.com/question/21801547");
        strings.add("https://www.zhihu.com/question/59330889/answer/466879495");
        strings.add("https://www.zhihu.com/question/59330889/answer/466879495");
        strings.add("https://www.zhihu.com/question/332330916");
        strings.add("https://www.zhihu.com/question/332330916");
        strings.add("https://www.zhihu.com/question/39635424");
        strings.add("https://www.zhihu.com/question/366674134/answer/979213821");
        strings.add("https://www.zhihu.com/question/39635424");
        strings.add("https://zhuanlan.zhihu.com/p/35079343");
        strings.add("https://zhuanlan.zhihu.com/p/35079343");
        strings.add("https://www.zhihu.com/question/49400499/answer/336098835");
        strings.add("https://www.zhihu.com/question/49400499/answer/336098835");
        strings.add("https://zhuanlan.zhihu.com/p/71281946");
        strings.add("https://www.zhihu.com/question/34998162/answer/171177623");
        strings.add("https://www.zhihu.com/question/34998162/answer/538693539");
        strings.add("https://www.zhihu.com/question/57300603?sort=created");
        strings.add("https://www.zhihu.com/question/305305325");
        strings.add("https://www.zhihu.com/question/23500766");
        strings.add("https://www.zhihu.com/question/33310955?sort=created&page=2");
        strings.add("https://www.zhihu.com/question/317938691");
        strings.add("https://www.zhihu.com/question/317938691");
        strings.add("https://www.zhihu.com/question/361161126");
        strings.add("https://www.zhihu.com/question/391107749/answer/1185441898");
        strings.add("https://www.zhihu.com/question/391107749/answer/1185492928");
        strings.add("https://www.zhihu.com/question/301267187");
        strings.add("https://www.zhihu.com/question/312566695/answer/607656982");
        strings.add("https://www.zhihu.com/question/312566695/answer/600702775");
        strings.add("https://www.zhihu.com/question/312566695/answer/607656982");
        strings.add("https://www.zhihu.com/question/40439934");
        strings.add("https://www.zhihu.com/question/40439934");
        strings.add("https://www.zhihu.com/question/40439934");
        strings.add("https://www.zhihu.com/question/40439934?sort=created");
        strings.add("https://www.zhihu.com/question/58360904/answer/814004176");
        strings.add("https://www.zhihu.com/question/20842469?sort=created");
        strings.add("https://www.zhihu.com/question/344463258");
        strings.add("https://www.zhihu.com/question/64222141");
        strings.add("https://www.zhihu.com/question/39238580");
        strings.add("https://www.zhihu.com/question/39238580");
        strings.add("https://zhuanlan.zhihu.com/p/129980599");
        strings.add("https://zhuanlan.zhihu.com/p/51808858");
        strings.add("https://zhuanlan.zhihu.com/p/103283047");
        strings.add("https://www.zhihu.com/question/263947314/answer/763297892");
        strings.add("https://www.zhihu.com/question/263947314/answer/763297892");
        strings.add("https://www.zhihu.com/question/360928026/answer/1029713078");
        strings.add("https://www.zhihu.com/question/277117387/answer/406748914");
        strings.add("https://zhuanlan.zhihu.com/p/100071499");
        strings.add("https://www.zhihu.com/question/21009978");
        strings.add("https://www.zhihu.com/question/21009978/answer/355244950");
        strings.add("https://www.zhihu.com/question/21009978");
        strings.add("https://www.zhihu.com/question/298800194/answer/513286475");
        strings.add("https://www.zhihu.com/question/298800194/answer/513286475");
        strings.add("https://www.zhihu.com/question/20269949/answer/229758586");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://zhuanlan.zhihu.com/p/28365675");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://www.zhihu.com/question/20269949/answer/229758586");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://www.zhihu.com/question/20269949?sort=created");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://www.zhihu.com/question/33415555/answer/814182615");
        strings.add("https://www.zhihu.com/question/374422605");
        strings.add("https://www.zhihu.com/question/374422605");
        strings.add("https://zhuanlan.zhihu.com/p/98393623?from_voters_page=true");
        strings.add("https://www.zhihu.com/question/20269949");
        strings.add("https://www.zhihu.com/question/373611790");
        strings.add("https://www.zhihu.com/question/360928026/answer/1029713078");
        strings.add("https://www.zhihu.com/question/360928026/answer/1029713078");
        strings.add("https://zhuanlan.zhihu.com/p/29227432");
        strings.add("https://zhuanlan.zhihu.com/p/29227432");
        strings.add("https://zhuanlan.zhihu.com/p/29227432");
        strings.add("https://zhuanlan.zhihu.com/p/29227432");
        strings.add("https://zhuanlan.zhihu.com/p/82232949");
        strings.add("https://www.zhihu.com/question/270814350");
        strings.add("https://www.zhihu.com/question/374138271/answer/1035827690");
        strings.add("https://www.zhihu.com/question/22384505");
        strings.add("https://www.zhihu.com/question/22384505/answer/410310550");
        strings.add("https://www.zhihu.com/question/323755316");
        strings.add("https://www.zhihu.com/question/323755316");
        strings.add("https://www.zhihu.com/question/26483051");
        strings.add("https://www.zhihu.com/question/26483214");
        strings.add("https://www.zhihu.com/question/65212825");
        strings.add("https://www.zhihu.com/question/23748037");
        strings.add("https://www.zhihu.com/question/48166424");
        strings.add("https://www.zhihu.com/question/65147551/answer/228105722");
        strings.add("https://www.zhihu.com/question/41784534");
        strings.add("https://www.zhihu.com/question/41784534");
        strings.add("https://www.zhihu.com/question/52486865/answer/671284273");
        strings.add("https://www.zhihu.com/zvideo/1193548145206837248?utm_source=qq");
        strings.add("https://www.zhihu.com/zvideo/1193548145206837248?utm_source=qq");
        strings.add("https://www.zhihu.com/question/33048035/answer/550982089");
        strings.add("https://www.zhihu.com/question/33048035/answer/550982089");
        strings.add("https://www.zhihu.com/question/266333633?sort=created");
        strings.add("http://zhihu.com/question/20840874/answer/1122650988");
        strings.add("http://zhihu.com/question/20840874/answer/1226604507");
        strings.add("http://zhihu.com/question/20840874/answer/1204445228");
        strings.add("https://www.zhihu.com/question/296525486");
        strings.add("https://www.zhihu.com/question/296525486");
        strings.add("https://www.zhihu.com/question/296525486");
        strings.add("https://zhuanlan.zhihu.com/p/21870915");
        strings.add("https://zhuanlan.zhihu.com/p/21870915");
        strings.add("https://www.zhihu.com/question/341012168/answer/793555334");
        strings.add("https://www.zhihu.com/question/371432675");
        strings.add("https://www.zhihu.com/question/27920118");
        strings.add("https://www.zhihu.com/question/284997833");
        strings.add("https://zhuanlan.zhihu.com/p/79874498");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://www.zhihu.com/question/41784534");
        strings.add("https://zhuanlan.zhihu.com/p/62945164");
        strings.add("https://zhuanlan.zhihu.com/p/62945164");
        strings.add("https://www.zhihu.com/question/50165063");
        strings.add("https://zhuanlan.zhihu.com/p/98654204");
        strings.add("https://www.zhihu.com/question/38433674");
        strings.add("https://www.zhihu.com/question/356687962");
        strings.add("https://www.zhihu.com/question/19952903?_=1493733970");
        strings.add("https://www.zhihu.com/question/19952903");
        strings.add("https://www.zhihu.com/question/19952903");
        strings.add("https://www.zhihu.com/question/20668251");
        strings.add("https://zhuanlan.zhihu.com/p/84218415");
        strings.add("https://www.zhihu.com/question/57808448");
        strings.add("https://www.zhihu.com/question/20842469/answer/1094190727");
        strings.add("https://www.zhihu.com/question/20842469");
        strings.add("https://www.zhihu.com/question/284997833");
        strings.add("https://zhuanlan.zhihu.com/p/56576129");
        strings.add("https://www.zhihu.com/question/65985957/answer/236830807");
        strings.add("https://www.zhihu.com/question/20842469/answer/485860355");
        strings.add("https://www.zhihu.com/question/20842469");
        strings.add("https://www.zhihu.com/question/62521787");
        strings.add("https://www.zhihu.com/question/62521787");
        strings.add("https://zhuanlan.zhihu.com/p/56576129");
        strings.add("https://www.zhihu.com/question/348052670/answer/838986212");
        strings.add("https://zhuanlan.zhihu.com/p/56576129");
        strings.add("https://www.zhihu.com/question/48436564");
        strings.add("https://www.zhihu.com/question/339353773/answer/785163409");
        strings.add("https://zhuanlan.zhihu.com/p/41219440");
        strings.add("https://zhuanlan.zhihu.com/p/41219440");
        strings.add("https://zhuanlan.zhihu.com/p/103771913?utm_source=wechat_session");
        strings.add("https://www.zhihu.com/question/29392422");
        strings.add("https://zhuanlan.zhihu.com/p/67871644");
        strings.add("https://www.zhihu.com/question/24002043");
        strings.add("https://www.zhihu.com/question/353524121");
        strings.add("https://www.zhihu.com/question/24002043/answer/1107890958");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://www.zhihu.com/question/24002043");
        strings.add("https://www.zhihu.com/question/353524121");
        strings.add("https://www.zhihu.com/question/26544246");
        strings.add("https://www.zhihu.com/question/24002043/answer/1107890958");
        strings.add("https://www.zhihu.com/question/24002043?sort=created");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://www.zhihu.com/question/26544246");
        strings.add("https://www.zhihu.com/question/24002043/answer/290225339");
        strings.add("https://www.zhihu.com/question/297277921/answer/512685885");
        strings.add("https://www.zhihu.com/question/23134558");
        strings.add("https://www.zhihu.com/question/348052670/answer/838986212");
        strings.add("https://www.zhihu.com/question/21993463");
        strings.add("https://www.zhihu.com/question/21993463/answer/382266000");
        strings.add("https://www.zhihu.com/question/38157817");
        strings.add("https://www.zhihu.com/question/359138516");
        strings.add("https://www.zhihu.com/question/359138516");
        strings.add("https://www.zhihu.com/question/23932999");
        strings.add("https://zhuanlan.zhihu.com/p/78829415");
        strings.add("https://www.zhihu.com/question/360415164/answer/1063666303");
        strings.add("https://www.zhihu.com/question/66162251");
        strings.add("https://www.zhihu.com/question/23932999");
        strings.add("https://www.zhihu.com/question/46923879");
        strings.add("https://www.zhihu.com/question/46923879");
        strings.add("https://www.zhihu.com/question/39071195/answer/347824306");
        strings.add("https://www.zhihu.com/question/39071195");
        strings.add("https://www.zhihu.com/question/376059488/answer/1051207936");
        strings.add("https://zhuanlan.zhihu.com/p/51808858");
        strings.add("https://www.zhihu.com/question/54272793");
        strings.add("https://www.zhihu.com/question/54272793");
        strings.add("https://www.zhihu.com/question/50260049/answer/130627876");
        strings.add("https://www.zhihu.com/question/50260049/answer/130627876");
        strings.add("https://www.zhihu.com/question/50260049?sort=created");
        strings.add("https://www.zhihu.com/question/50260049/answer/130627876");
        strings.add("https://www.zhihu.com/question/50260049/answer/130627876");
        strings.add("https://www.zhihu.com/question/50260049/answer/130627876");
        strings.add("https://www.zhihu.com/question/50260049");
        strings.add("https://www.zhihu.com/question/63103526");
        strings.add("https://zhuanlan.zhihu.com/p/102149792");
        strings.add("https://zhuanlan.zhihu.com/p/92588356");
        strings.add("https://zhuanlan.zhihu.com/p/139350158");
        strings.add("https://www.zhihu.com/question/21023376/answer/797154942");
        strings.add("https://www.zhihu.com/people/wuxiaowuxiaoer/activities");
        strings.add("https://www.zhihu.com/people/wuxiaowuxiaoer");
        strings.add("https://www.zhihu.com/question/292552240");
        strings.add("https://www.zhihu.com/people/deng-bo-4-29/answers");
        strings.add("https://www.zhihu.com/question/360928026/answer/1029713078");
        strings.add("https://www.zhihu.com/question/296525486");
        strings.add("https://www.zhihu.com/question/296525486");
        strings.add("https://www.zhihu.com/question/48759272/answer/654353796");
        strings.add("https://www.zhihu.com/question/374138271/answer/1035827690");
        strings.add("https://www.zhihu.com/question/366674134/answer/979213821");
        strings.add("https://zhuanlan.zhihu.com/p/102149792");
        strings.add("http://zhuanlan.zhihu.com/p/141569019?from_voters_page=true");
        strings.add("http://zhuanlan.zhihu.com/p/141569019?from_voters_page=true");
        strings.add("http://zhuanlan.zhihu.com/p/141569019?from_voters_page=true");
        strings.add("http://zhuanlan.zhihu.com/p/141569019?from_voters_page=true");
        strings.add("https://zhuanlan.zhihu.com/p/47089554");
        strings.add("https://zhuanlan.zhihu.com/p/47089554");
        strings.add("https://zhuanlan.zhihu.com/p/100674530");
        strings.add("https://zhuanlan.zhihu.com/p/100674530");
        strings.add("https://zhuanlan.zhihu.com/p/100674530");
        strings.add("https://www.zhihu.com/question/366484528/answer/977128860");
        strings.add("https://www.zhihu.com/question/386940897");
        strings.add("https://www.zhihu.com/topic/19829014/hot");
        strings.add("https://www.zhihu.com/question/290703077");
        strings.add("https://zhuanlan.zhihu.com/p/82567819?utm_source=wechat_session");
        strings.add("http://zhuanlan.zhihu.com/p/141851277?from_voters_page=true");

        for (String string : strings) {
            System.out.println(string);
            int qid = findQid(string, prefix);

            System.out.println(qid);
        }

    }

    private static int findQid(String content, String prefix) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }
        if (!content.contains(prefix)) {
            return 0;
        }


        //正则表达式，用于匹配非数字串，+号用于匹配出多个非数字串
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        boolean b = matcher.find();
        if (b) {
            String group = matcher.group(0);
            return Integer.parseInt(group);
        } else {
            return 0;
        }
    }

    @Transactional
    public int save(KeywordBaiduSearchResult searchResult) {
        int qid = findQid(searchResult.getUrlReal(), prefix);
        Example example = new Example(ZhihuQuestionBasic.class);
        example.createCriteria().andEqualTo("qid",qid);
        int count = mapper.selectCountByExample(example);
        if (count >0){
            return 1;
        }

        ZhihuQuestionBasic zhihuQuestionBasic = new ZhihuQuestionBasic();
        zhihuQuestionBasic.setQid(qid);
        Date date = new Date();
        zhihuQuestionBasic.setCreateTime(date);
        zhihuQuestionBasic.setUpdateTime(date);
        return mapper.insert(zhihuQuestionBasic);
    }

    public List<ZhihuQuestionBasic> findNoParse(int limit) {
        List<ZhihuQuestionBasic> list = mapper.findNoParseZhihu(limit);

        return list;
    }


    public int update(ZhihuQuestionBasic basic) {
        return mapper.updateByPrimaryKey(basic);
    }
}

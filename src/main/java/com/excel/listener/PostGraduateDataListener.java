package com.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.excel.dao.DemoDAO;
import com.excel.dao.PostGraduateDAO;
import com.excel.entity.DemoData;
import com.excel.entity.PostGraduate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostGraduateDataListener extends AnalysisEventListener<PostGraduate> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
//    private static final int BATCH_COUNT = 5;
//    int count =0;
    List<PostGraduate> list = new ArrayList<PostGraduate>();
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private PostGraduateDAO postGraduateDAO;
    public PostGraduateDataListener() {
        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
        postGraduateDAO = new PostGraduateDAO();
    }
    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param demoDAO
     */
    public PostGraduateDataListener(PostGraduateDAO postGraduateDAO) {
        this.postGraduateDAO =postGraduateDAO ;
    }
    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(PostGraduate data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
        list.add(data);
//        count++;
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
//        if (list.size() >= BATCH_COUNT) {
//            saveData();
//            // 存储完成清理 list
//            list.clear();
//        }
    }
    public List differentList(String str){
        List<PostGraduate> selectedList = list.stream()
                .filter(e -> e.getMajor_name().equals(str))
                .collect(Collectors.toList());
        return selectedList;
    }

    public Map<String,String> countFinal(List<PostGraduate> gras){
        Map<String,String> map = new HashMap<>();
        List<Integer> preExamsSumScores = gras.stream().map(e -> e.getPreExamSumScore()).collect(Collectors.toList());
        map.put("初试总成绩",count(preExamsSumScores));
        List<Integer> postWrittenScores = gras.stream().map(e -> e.getPostWrittenScore()).collect(Collectors.toList());
        map.put("复试笔试成绩",count(postWrittenScores));
        List<Integer> postComputerScores = gras.stream().map(e -> e.getPostComputerScore()).collect(Collectors.toList());
        map.put("复试机试成绩",count(postComputerScores));
        List<Double> postInterviewScores = gras.stream().map(e -> e.getPostInterviewScore()).collect(Collectors.toList());
        map.put("复试面试分",countDouble(postInterviewScores));
        List<Double> postSumScores = gras.stream().map(e -> e.getPostSumScore()).collect(Collectors.toList());
        map.put("复试总分",countDouble(postSumScores));
        List<Double> totalScores = gras.stream().map(e -> e.getTotalScore()).collect(Collectors.toList());
        map.put("总分",countDouble(totalScores));
        return map;
    }

    String countDouble(List<Double> list){
        double sum =0;
        double avg =0;
        Collections.sort(list);
        double max = list.get(list.size()-1);
//        double min = list.get(0);
        double min = 0;
        for (int i = 0; i <list.size() ; i++) {
            if (list.get(i)>min){
                min = list.get(i);
                break;
            }
        }
        for (int i = 0; i <list.size() ; i++) {
            sum+=list.get(i);
        }
        avg = sum/list.size();
        double variance = 0;
        for (int i = 0; i <list.size() ; i++) {
            variance+= (list.get(i)-avg)*(list.get(i)-avg);
        }
        variance = variance/list.size();
        StringBuffer sb = new StringBuffer();
        sb.append(min).append("--").append(max).
                append("--").append(avg).append("--")
                .append(variance);
        return sb.toString();
    }

    /**
     * 要算出来平均数 最大值 最小值 方差
     * */
    String count(List<Integer> list){
        int sum = 0;
        int avg = 0;
        Collections.sort(list);
        int max = list.get(list.size()-1);
//        int min = list.get(0);
        int min= 0;
        for (int i = 0; i <list.size() ; i++) {
            if (list.get(i)>min){
                min = list.get(i);
                break;
            }
        }
        for (int i = 0; i <list.size() ; i++) {
            sum+=list.get(i);
        }
        avg =sum/list.size();
        int variance = 0;
        for (int i = 0; i <list.size() ; i++) {
            variance+= (list.get(i)-avg)*(list.get(i)-avg);
        }
        variance = variance/list.size();
        StringBuffer sb = new StringBuffer();
        sb.append(min).append("--").append(max).
                append("--").append(avg).append("--")
                .append(variance);
        return sb.toString();
    }
    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        List cs = differentList("电子信息（软件工程）");
        Map computerScience = countFinal(cs);
        System.out.println(computerScience);

        saveData();
//        LOGGER.warn("{}条数据",count);
//        System.out.println(count);
        LOGGER.info("所有数据解析完成！");
    }
    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
        postGraduateDAO.save(list);
        LOGGER.info("存储数据库成功！");
    }
}

package demand.general;

import lombok.Data;
import model.LabelAndCount;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数量控制
 *
 * @Author: zhuzw
 * @Date: 2019/9/10 14:53
 * @Version: 1.0
 */
@Data
public class CountControl {

    /**
     * 用于控制
     */
    private HashMap<String, Integer> controlMap = new HashMap<>();

    /**
     * 用于单文件提取数据
     */
    public CountControl() {
    }

    /**
     * 构造函数，可以传入hashMap以控制每个分类提取的数量
     * @param controlMap
     */
    public CountControl(HashMap<String, Integer> controlMap) {
        this.controlMap = controlMap;
    }

    /**
     * 操作状态
     *
     * @param label 控制的key
     * @param count 数量
     * @param categoryNum 分类数量
     * @return 1、数据量不够，继续操作
     *          0、该分类数据量够，跳过操作
     *          -1、数据量收集完毕，终止操作
     */
    public int operationStatus(String label, int count, int categoryNum) {
       if (null == controlMap.get(label)) {
           //初始化
           controlMap.put(label, 0);
       }
        //-1、所有数据够
        int toolNum = 0;
        for (Map.Entry<String, Integer> entry : controlMap.entrySet()) {
            toolNum += entry.getValue();
        }
        if (toolNum >= count * categoryNum) {
            return -1;
        }
        //1、数据不够情况
       if (controlMap.get(label) < count) {
           controlMap.put(label, controlMap.get(label) + 1);
           return 1;
       }
       //0、该类分类数据够情况
       return 0;
    }

    /**
     * 获取提取情况
     * label: 数量
     * @return
     */
    public List<String> getExtract() {
        //controlMap转成List<LabelAndCount>对象后对label排序，后转成List<String>对象
        List<String> resultList = controlMap.entrySet().stream().map(temp -> {
           return new LabelAndCount(Integer.valueOf(temp.getKey()), temp.getValue());
        }).sorted(Comparator.comparing(LabelAndCount::getLabel))
                .map(LabelAndCount::toString)
                .collect(Collectors.toList());

        return resultList;
    }

    /**
     *
     * @param lessCount
     * @return
     */
    public List<String> filterLess(int lessCount) {
        //controlMap转成List<LabelAndCount>对象后对label排序，后转成List<String>对象
        List<LabelAndCount> resultList = controlMap.entrySet().stream().filter(temp->{
            return temp.getValue() < lessCount;
        }).map(temp -> {
            return new LabelAndCount(Integer.valueOf(temp.getKey()), temp.getValue());
        }).sorted(Comparator.comparing(LabelAndCount::getLabel))
                .collect(Collectors.toList());

        List<String> resultStrings = resultList.stream()
                .filter(labelAndCount-> labelAndCount.getCount() > lessCount)
                .map(LabelAndCount::toString)
                .collect(Collectors.toList());

        return resultStrings;
    }
}

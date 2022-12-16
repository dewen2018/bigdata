// package com.dewen.wordcount.sink;
//
// import com.alibaba.fastjson.JSONObject;
// import com.alibaba.fastjson.serializer.SerializerFeature;
// import com.shengekeji.simulator.dao.BestDispatchDao;
// import com.shengekeji.simulator.dao.DriverDao;
// import com.shengekeji.simulator.model.DispatchModel;
// import com.shengekeji.simulator.model.DriverModel;
// import com.shengekeji.simulator.model.GeographyOrder;
// import com.shengekeji.simulator.model.PassagesModel;
// import org.apache.flink.streaming.api.functions.source.SourceFunction;
// import org.apache.ibatis.session.SqlSession;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
//
// public class DemoMySQLDriverSource implements SourceFunction<String> {
//
//     private static final long serialVersionUID = 1L;
//
//     private volatile boolean isRunning = true;
//
//     /**
//      * 此处是代码的关键，要从mysql表中，把数据读取出来
//      * @param sourceContext
//      * @throws Exception
//      */
//
//     @Override
//     public void run(SourceContext<String> sourceContext) throws Exception {
//         while(this.isRunning) {
//             Thread.sleep(5000);
//             System.out.println("--------------------------");
//             SqlSession sqlSession = null;
//             Map<String,Object> map = new HashMap<String, Object>();
//             map.put("appId","SGKJ");
//             try {
//                 sqlSession = MyBatisUtil.openSqlSession();
//                 // 通过SqlSession对象得到Mapper接口的一个代理对象
//                 // 需要传递的参数是Mapper接口的类型
//                 //司机信息数据
//                 DriverDao driverdao = sqlSession.getMapper(DriverDao.class);
//                 List<DriverModel> drivers = driverdao.selectAllActiveDriver(map);
//                 //处理每个司机
//                 for (DriverModel driver:drivers){
//                     driver.setLoc(new LngLat(locLongitude,locLatitude));
//                     driver.setSendTime(RandomUtil.getTimeStr());
//                     String dr = JSONObject.toJSONString(driver, SerializerFeature.DisableCircularReferenceDetect);
//                     System.out.println(dr);
//                     sourceContext.collect(dr);
//                 }
//
//             }catch (Exception e){
//                 e.printStackTrace();
//                 System.err.println(e.getMessage());
//                 sqlSession.rollback();
//             }finally {
//
//                 if (sqlSession != null){
//                     sqlSession.close();
//                 }
//
//             }
//         }
//     }
//
//     @Override
//     public void cancel() {
//         this.isRunning = false;
//     }
//
// }

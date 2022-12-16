// package com.dewen.wordcount.sink;
//
// import com.alibaba.fastjson.JSONObject;
// import com.dewen.eCommercePlatform.entity.SubOrderDetail;
// import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
// import org.apache.ibatis.session.SqlSession;
// import com.shengekeji.simulator.model.OrderModel;
//
// public class OrderMySqlSink extends RichSinkFunction<String> {
//
//     @Override
//     public void invoke(String value, Context context) throws Exception {
//         SqlSession sqlSession = null;
//         try {
//             SubOrderDetail order= JSONObject.parseObject(value, SubOrderDetail.class);
//             sqlSession = MyBatisUtil.openSqlSession();
//             // 通过SqlSession对象得到Mapper接口的一个代理对象
//             // 需要传递的参数是Mapper接口的类型
//             OrderDao dao = sqlSession.getMapper(OrderDao.class);
//             dao.insert(order);
//             sqlSession.commit();
//         }catch (Exception e){
//             e.printStackTrace();
//             System.err.println(e.getMessage());
//             sqlSession.rollback();
//
//         }finally {
//
//             if (sqlSession != null){
//                 sqlSession.close();
//             }
//         }
//     }
// }

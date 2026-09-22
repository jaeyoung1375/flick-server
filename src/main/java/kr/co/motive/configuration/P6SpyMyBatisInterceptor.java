package kr.co.motive.configuration;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.MDC;
@Intercepts({
    // SELECT
    @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
    ),
    // INSERT / UPDATE / DELETE
    @Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
    )
})
public class P6SpyMyBatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        // Mapper ID를 MDC에 넣어서 P6Spy 커스텀 포맷터에서 사용 가능
        MDC.put("P6SPY_CATEGORY", ms.getId());
        try {
            return invocation.proceed();
        } finally {
            MDC.remove("P6SPY_CATEGORY");
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 필요 시 properties 사용 가능
    }
}
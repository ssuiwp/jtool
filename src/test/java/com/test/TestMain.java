package com.test;

import com.yl.design.fun.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author suiwp
 * @date 2025/4/1 11:17
 */
public class TestMain {
    public static void main(String[] args) {

        Result<Err, Object> noneData = Result.err(Err.NONE_DATA_ERR, "none data");


        Logger log = LoggerFactory.getLogger("test_log");
        testLog(log, noneData);

        Result<Err, Object> noneDataTh = Result.err(Err.NONE_DATA_ERR, "none data", new Exception("eeeeee"));
        testLog(log, noneDataTh);
        if (noneDataTh.isErr()) {
            Err errCode = Result.DealErr.getErrCode(noneDataTh);
            switch (errCode) {
                case NONE_DATA_ERR:
                    break;
                case IO_ERR:
                    break;
                case NO_FILE_ERR:
                    break;
                default:

            }
        }
    }

    public static void testLog(Logger log, Result<?, ?> result) {
        log.error(Result.DealErr.getErrMsg(result));
        System.out.println("------------------------------");
        log.error(Result.DealErr.getLogMsg("task_run", result));
        System.out.println("----------------------------");
        log.error(Result.DealErr.getErrCode(result).name());
        System.out.println("---------------------");
        log.error(Result.DealErr.getMsgOfThrowable(result));
        System.out.println("-------------------------");
        log.error(Result.DealErr.getLogMsgOfThrowable("task_run", result));
        System.out.println("----------------------------\n");
    }

    public enum Err {
        NONE_DATA_ERR,
        IO_ERR,
        NO_FILE_ERR,
        ;
    }
}

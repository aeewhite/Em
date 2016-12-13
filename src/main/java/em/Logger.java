package em;

/**
 * Log handler with custom log levels
 *
 * Log Level 0 = only important messages and errors
 * Log level 1 = debug messages
 * Log level 2 = silly
 *
 *
 * Created by andrew on 9/24/16.
 */
public class Logger {

    private static int logLevel;

    public static void setLogLevel(int level){
        logLevel = level;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    private static void log(int level, String logMessage){
        if(logLevel < 0){
            System.err.println(logMessage);
        }
        else if(level <= logLevel){
            System.out.println(logMessage);
        }
    }

    public static void info(Object message){
        log(0, message.toString());
    }

    public static void debug(Object message){
        log(1, message.toString());
    }

    public static void silly(Object message){
        log(2, message.toString());
    }

    public static void error(Object message){
        log(-1, message.toString());
    }

}

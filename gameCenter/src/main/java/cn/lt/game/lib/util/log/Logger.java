package cn.lt.game.lib.util.log;

/**
 * Logger is a wrapper of {@link android.util.Log}
 * But more pretty, simple and powerful
 *
 * @author Orhan Obut
 */
public final class Logger {

    private static final Printer printer     = new LoggerPrinter();
    private static final String  DEFAULT_TAG = "PRETTYLOGGER";

    private static boolean debug = true;

    //no instance
    private Logger() {
    }

    /**
     * It is used to get the settings object in order to change settings
     *
     * @return the settings object
     */
    public static Settings init() {
        return printer.init(DEFAULT_TAG);
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger
     */
    public static Settings init(String tag) {
        return printer.init(tag);
    }

    public static Printer t(String tag) {
        return printer.t(tag, printer.getSettings().getMethodCount());
    }

    public static Printer t(int methodCount) {
        return printer.t(null, methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    public static void d(String message, Object... args) {
        if (debug)
            printer.d(message, args);
    }

    public static void e(String message, Object... args) {
        if (debug)printer.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (debug)printer.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        if (debug) printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        if (debug)printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        if (debug)printer.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        if (debug)printer.wtf(message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        if (debug)printer.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        if (debug)printer.xml(xml);
    }

    /**
     * close logger,can not print log
     */
    public static void close() {
        debug = false;
    }

    /**
     * open logger,can print log
     */
    public static void open(){
        debug = true;
    }

}

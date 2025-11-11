-if class com.cforce.reminder.data.ApiResponse
-keepnames class com.cforce.reminder.data.ApiResponse
-if class com.cforce.reminder.data.ApiResponse
-keep class com.cforce.reminder.data.ApiResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi,java.lang.reflect.Type[]);
}

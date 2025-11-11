-if class com.cforce.reminder.data.Contest
-keepnames class com.cforce.reminder.data.Contest
-if class com.cforce.reminder.data.Contest
-keep class com.cforce.reminder.data.ContestJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}

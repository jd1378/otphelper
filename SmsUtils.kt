import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.database.Cursor

fun markSmsAsRead(context: Context, messageId: Long) {
    val cr: ContentResolver = context.contentResolver
    val uri: Uri = Uri.parse("content://sms/")
    val projection = arrayOf("_id")
    val selection = "_id = ?"
    val selectionArgs = arrayOf(messageId.toString())
    val cursor: Cursor? = cr.query(uri, projection, selection, selectionArgs, null)
    if (cursor == null || !cursor.moveToFirst()) {
        Log.d("markSmsAsRead", "No match found: $messageId")
    } else {
        val cv = ContentValues()
        cv.put("read", true)
        cr.update(uri, cv, "_id = ?", arrayOf(messageId.toString()))
    }
    cursor?.close()
}

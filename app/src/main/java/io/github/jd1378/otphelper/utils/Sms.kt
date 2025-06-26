package io.github.jd1378.otphelper.utils

import android.content.ContentValues
import android.content.Context
import android.provider.Telephony
import androidx.core.net.toUri

fun getMessageId(context: Context, message: String): Long {
  val projection = arrayOf("_id", "body")
  val selection = "body = ?"
  val selectionArgs = arrayOf(message)
  val uri = "content://sms/inbox".toUri()
  context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
    if (cursor.moveToFirst()) {
      val idIndex = cursor.getColumnIndex("_id")
      if (idIndex != -1) {
        return cursor.getLong(idIndex)
      }
    }
  }
  return -1
}

/**
 * Mark a SMS message as being read
 *
 * @param context
 * @param messageID - The Message ID that we want to alter.
 * @return boolean - Returns true if the message was updated successfully.
 */
fun setMessageRead(context: Context, messageID: Long, isRead: Boolean): Boolean {
  if (messageID == -1L) {
    return false
  }
  val contentValues = ContentValues()
  contentValues.put(
      Telephony.Sms.READ,
      if (isRead) {
        1
      } else {
        0
      },
  )
  return try {
    context.contentResolver.update(
        Telephony.Sms.CONTENT_URI,
        contentValues,
        Telephony.Sms._ID + " = ?",
        arrayOf(messageID.toString()),
    ) > 0
  } catch (_: Throwable) {
    false
  }
}

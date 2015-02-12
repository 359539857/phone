package com.tandong.workhour.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
	public DateUtil() {
	};

	/**
	 * 很多模板可能也需要调用这边的方法， 所以进行实例化一个实例
	 */
	public static final DateUtil instance = new DateUtil();

	private static Logger log = Logger.getLogger(DateUtil.class);

	private static final Calendar utilCal = Calendar.getInstance(); // 用于时间格式的转换

	public static final int MILLISECOND_PER_SECOND = 1000;
	public static final int MILLISECOND_PER_MINUTE = 60 * 1000;
	public static final int MILLISECOND_PER_HOUSE = 60 * 60 * 1000;
	public static final int MILLISECOND_PER_DAY = 24 * 60 * 60 * 1000;

	public static int getTotalOfDays(int month, int year) {
		int dom[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

		if (year % 400 == 0) {
			dom[1] = 29;
		} else {
			if (year % 4 == 0 && year % 100 != 0) {
				dom[1] = 29;
			}
		}
		return dom[month - 1];
	}

	public static String convertDatetimeFormat(Date inputDate) {

		SimpleDateFormat newDateFormat = new SimpleDateFormat("HHmmss");
		String newDateString = newDateFormat.format(inputDate);

		return newDateString;

	}

	public static String format(Date date, String format) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat(format);
		return newDateFormat.format(date);
	}

	public static String formatDate(Date date) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return newDateFormat.format(date);
	}

	public static String formatTime(Date date) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return newDateFormat.format(date);
	}

	public static String format(String date, String format) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat(format);
		try {
			Date $date = newDateFormat.parse(date);
			return newDateFormat.format($date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date parseDate(String date) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date $date = newDateFormat.parse(date);
			return $date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date parseTime(String time) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date $date = newDateFormat.parse(time);
			return $date;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 把String类型的日期格式进行转换， 格式为 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date convertString2Date(String strDate) {
		SimpleDateFormat newDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = newDateFormat.parse(strDate);
		} catch (ParseException e) {
			log.error("parse date wrong[" + strDate + "]", e);
		}
		return date;
	}

	/**
	 * 根据知道的时间格式转换日期
	 * 
	 * @param strDate
	 * @param pattern
	 * @return
	 */
	public static Date convertString2Date(String strDate, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			return dateFormat.parse(strDate);
		} catch (ParseException e) {
			log.error("parse date wrong[" + strDate + "] " + e);
			return null;
		}
	}

	/**
	 * 根据指定的时间格式把时间转换成string
	 * 
	 * @param inputDate
	 * @param pattern
	 * @return
	 */
	public static String convertDate2String(Date inputDate, String pattern) {
		if (inputDate == null)
			return null;
		SimpleDateFormat newDateFormat = new SimpleDateFormat(pattern);
		return newDateFormat.format(inputDate);
	}

	/**
	 * 格式类似 yyyy-MM-dd
	 * 
	 * @param value
	 * @return
	 */
	public static Date doConvertToDate(String value) {
		Date result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			result = sdf.parse((String) value);
		} catch (ParseException e) {
			log.error("时间转换出错", e);
		}
		return result;
	}

	/**
	 * 把日期转换成当天的最后时间， 即取到当天参数之前的日期， 即 今天是2006-6-24当parm参数值为3的时候返回的就是27号
	 * 
	 * @param date
	 * @return
	 */
	public static java.util.Date parse2EndDate(java.util.Date date) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	public static String convertEndDateFormat(Date inputDate) {

		SimpleDateFormat newDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd 23:59:59:999");
		String newDateString = newDateFormat.format(inputDate);

		return newDateString;

	}

	public static String convertFormatDate(Date inputDate) {

		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String newDateString = newDateFormat.format(inputDate);

		return newDateString;

	}

	public static int campareToParm(Date parm) {
		Date date = new Date();

		return date.compareTo(parm);
	}

	/**
	 * 时间的比较， 值为-1, 0, 1
	 * 
	 * @param frontDate
	 *            比较大则为1
	 * @param backDate
	 *            比较大则为-1
	 * @return
	 */
	public static int compare(Date frontDate, Date backDate) {
		if (frontDate == null)
			return -1;
		if (frontDate instanceof Timestamp) { // frontDate可能是Timestamp类型
			backDate = new Timestamp(backDate == null ? 0 : backDate.getTime());
		}
		return frontDate.compareTo(backDate);
	}

	/**
	 * 取到当月的天数
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int parseCountDayOfMonth(Date date) {

		int dateCount = 0;
		int month = date.getMonth() + 1;
		if (month == 2) {
			dateCount = 28;

		} else if (month == 4 || month == 6 || month == 8 || month == 11) {
			dateCount = 30;

		} else if (month == 3 || month == 5 || month == 7 || month == 9
				|| month == 10 || month == 12) {
			dateCount = 31;
		}

		return dateCount;
	}

	/**
	 * 取到当天参数之前的日期， 即 今天是2006-6-24当parm参数值为3的时候返回的就是21号
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static java.util.Date parseDateForwardParm(int parm) {

		Calendar cal = Calendar.getInstance();

		Date date = new Date();
		cal.setTime(date);
		int month = date.getMonth() + 1;
		int temp = date.getDate() - parm;

		if (temp > 0) {

			cal.set(Calendar.DATE, temp);
			return cal.getTime();
		}

		else if (temp == 0) {
			if (month == 3) {

				cal.set(Calendar.DATE, 28);
				cal.set(Calendar.MONTH, 2 - 1);

			} else if (month == 4 || month == 6 || month == 8 || month == 11) {
				cal.set(Calendar.DATE, 30);
				cal.set(Calendar.MONTH, date.getMonth() - 1);

			} else if (month == 1) {
				cal.set(Calendar.MONTH, 12 - 1);
				cal.set(Calendar.DATE, 31);
				cal.set(Calendar.YEAR, date.getYear() - 1);
			} else if (month == 3 || month == 5 || month == 7 || month == 9
					|| month == 10) {
				cal.set(Calendar.MONTH, date.getMonth() - 1);
				cal.set(Calendar.DATE, 31);
			}

			// System.out.println(date);
		} else if (temp < 0) {
			if (month == 3) {
				int d = date.getDate() + 28 - parm;
				cal.set(Calendar.DATE, d);
				cal.set(Calendar.MONTH, 2 - 1);

			} else if (month == 4 || month == 6 || month == 8 || month == 11) {
				int d = date.getDate() + 30 - parm;
				cal.set(Calendar.MONTH, date.getMonth() - 1);
				cal.set(Calendar.DATE, d);

			} else if (month == 1) {
				int d = date.getDate() + 31 - parm;
				date.setYear(date.getYear() - 1);
				cal.set(Calendar.MONTH, 11);
				cal.set(Calendar.DATE, d);

			} else if (month == 3 || month == 5 || month == 7 || month == 9
					|| month == 10) {
				int d = date.getDate() + 31 - parm;
				cal.set(Calendar.MONTH, date.getMonth() - 1);
				cal.set(Calendar.DATE, d);
			}
		}
		return cal.getTime();
	}

	/**
	 * 取到参数的日期， 如果今天是二月23,参数是1就取到24号 向前来走进几天，
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static java.util.Date parseDateTOParm(int parm) {
		Calendar cal = Calendar.getInstance();

		Date date = new Date();
		cal.setTime(date);
		int month = date.getMonth() + 1;
		int temp = date.getDate() + parm;
		if (month == 2) {
			if (temp <= 28) {
				cal.set(Calendar.DATE, date.getDate() + parm);
			} else {
				cal.set(Calendar.DATE, (temp - 28));
				cal.set(Calendar.MONTH, date.getMonth() + 1);
			}
		} else if (month == 4 || month == 6 || month == 8 || month == 11) {

			if (temp <= 30) {
				cal.set(Calendar.DATE, date.getDate() + parm);
			} else {
				cal.set(Calendar.DATE, (temp - 30));
				cal.set(Calendar.MONTH, date.getMonth() + 1);
			}

		} else if (month == 12) {
			if (temp <= 31) {
				cal.set(Calendar.DATE, date.getDate() + parm);
			} else {
				cal.set(Calendar.DATE, (temp - 31));
				cal.set(Calendar.MONTH, 0);
				cal.set(Calendar.YEAR, date.getYear() + 1);
			}
		} else if (temp <= 31) {
			date.setDate(date.getDate() + parm);
		} else {
			date.setDate((temp - 31));
			cal.set(Calendar.MONTH, date.getMonth() + 1);
		}
		// System.out.println(date);

		return cal.getTime();
	}

	/**
	 * 把日期转换成当月的最早时间， 即 0：0：0 001
	 * 
	 * @param date
	 * @return
	 */
	public static java.util.Date parse2BeginDateOfMonth(java.util.Date date) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 001);
		return cal.getTime();
	}

	/**
	 * 把日期转换成当月的前一个月最早时间， 即 0：0：0 00１ 如果本月是六月就取到5月的最早时间
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static java.util.Date parse2BeginDateOfMonthLast(java.util.Date date) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.set(Calendar.MONTH, date.getMonth() - 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 001);
		return cal.getTime();
	}

	/**
	 * 取得当前星期几
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getWeek() {

		Calendar c = Calendar.getInstance();
		Date tmpDate = new Date();
		c.set(tmpDate.getYear(), tmpDate.getMonth(), tmpDate.getDay());
		int day = c.get(Calendar.DAY_OF_WEEK);
		String week = "";
		if (day == 0) {
			week = "星期2";
		} else if (day == 1) {
			week = "星期二";
		} else if (day == 2) {
			week = "星期三";
		} else if (day == 3) {
			week = "星期四";
		} else if (day == 4) {
			week = "星期五";
		} else if (day == 5) {
			week = "星期六";
		} else if (day == 6) {
			week = "星期日";
		} else if (day == 7) {
			week = "星期一";
		}
		return week;
	}

	/**
	 * 把日期转换成 parm =1 ,就是取到 1 月最早时间， 即 0：0：0 00１
	 * 
	 * @param date
	 * @return
	 */
	public static java.util.Date parse2BeginDateOfMonthParm(int parm) {
		if (parm == 0)
			return null;
		Date date = new Date();
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);
		cal.set(Calendar.MONTH, parm - 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 001);
		return cal.getTime();
	}

	/**
	 * 把日期转换成当天的最早时间， 即 0：0：0 001
	 * 
	 * @param date
	 * @return
	 */
	public static java.util.Date parse2BeginDate(java.util.Date date) {
		if (date == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 001);
		return cal.getTime();
	}

	/**
	 * 增加时间日期
	 * 
	 * @param originDate
	 *            要增加的日期
	 * @param incValue
	 *            增加的值
	 * @param incFlag
	 *            增加标志， 与Calendar的常量一致, 使用时用Calendar.YEAR, Calendar.MONTH等
	 * @return
	 */
	public static Date incrementDate(Date originDate, int incValue, int incFlag) {
		if (originDate == null) {
			throw new IllegalArgumentException(" 日期不能为空");
		}
		utilCal.setTime(originDate);
		utilCal.add(incFlag, incValue);
		return utilCal.getTime();
	}

	/**
	 * 获得现在时间， 主要供模板使用
	 * 
	 * @return
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 日期的比较
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int compareDate(Date d1, Date d2) {
		if (d1 == null || d2 == null)
			throw new IllegalArgumentException(" 比较的日期不能为空");
		return d1.compareTo(d2);
	}

	/**
	 * 计算月未日期
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getEndDate(int year, int month) {
		int day = getEndDay(year, month);
		return convert2Date(year, month, day);
	}

	public static int getEndDay(int year, int month) {
		int mon_day[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (IsLeapYear(year) && month == 2)
			return 29;
		else
			return (mon_day[month - 1]);
	}

	public static boolean IsLeapYear(int year) /*
												 * find out the year is leap
												 * year or not
												 */
	{
		if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0))
			return true;
		else
			return false;
	}

	@SuppressWarnings("deprecation")
	public static int getMonthValue(Date inputDtm) {
		return inputDtm.getMonth() + 1;
	}

	@SuppressWarnings("deprecation")
	public static int getYearValue(Date inputDtm) {
		return inputDtm.getYear() + 1900;
	}

	@SuppressWarnings("deprecation")
	public static int getDayValue(Date inputDtm) {
		return inputDtm.getDate();
	}

	@SuppressWarnings("deprecation")
	public static int getHoursValue(Date inputDtm) {
		return inputDtm.getHours();
	}

	@SuppressWarnings("deprecation")
	public static int getMinuteValue(Date inputDtm) {
		return inputDtm.getMinutes();
	}

	@SuppressWarnings("deprecation")
	public static int getSecondValue(Date inputDtm) {
		return inputDtm.getSeconds();
	}

	@SuppressWarnings("deprecation")
	public static int getWeekDayValue(Date inputDtm) {
		return inputDtm.getDay();
	}

	public static Date convert2Date(int year, int month, int day, int hour,
			int minutes, int sec) {
		// if(month<1 || month >12 || day<1 || day>31 || hour <1 || hour >23 ||
		// minutes <1 || minutes>59 || sec < 1 || sec > 59) //简单判断日期格式
		// throw new IllegalArgumentException(" 日期格式错误:[" + year + "-" + month +
		// "-" + day + " "+ hour +":" + minutes + ":" + sec + "]");
		utilCal.set(Calendar.YEAR, year);
		utilCal.set(Calendar.MONTH, month - 1);
		utilCal.set(Calendar.DATE, day);
		utilCal.set(Calendar.HOUR, hour);
		utilCal.set(Calendar.MINUTE, minutes);
		utilCal.set(Calendar.SECOND, sec);
		return utilCal.getTime();
	}

	public static Date convert2Date(int year, int month, int day) {
		utilCal.setTimeInMillis(System.currentTimeMillis());
		return convert2Date(year, month, day, utilCal.get(Calendar.HOUR),
				utilCal.get(Calendar.MINUTE), utilCal.get(Calendar.SECOND));
	}

	public static int convert2Int() {
		Date d = new Date();
		@SuppressWarnings("deprecation")
		int rtemp = d.getYear() + d.getMonth() + d.getDate();
		log.info("==============================" + rtemp
				+ "===================================");
		return rtemp;
	}

	/**
	 * 用于打印现在的时间所显示的格式， 到微秒
	 * 
	 * @return
	 */
	public static String timePrinting() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.toString();
	}

}

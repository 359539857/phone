var Cookie = function() {
	this.addCookie = function(c_name, value, expiredays) {
		var exdate = new Date()
		exdate.setDate(exdate.getDate() + expiredays)
		var cookieStr = c_name
				+ "="
				+ escape(value)
				+ ((expiredays == null) ? "" : ";expires="
						+ exdate.toGMTString());
		document.cookie = cookieStr;
	};

	this.getCookie = function(c_name) {
		if (document.cookie.length > 0) {
			var c_start = document.cookie.indexOf(c_name + "=")
			if (c_start != -1) {
				c_start = c_start + c_name.length + 1
				var c_end = document.cookie.indexOf(";", c_start)
				if (c_end == -1)
					c_end = document.cookie.length
				return unescape(document.cookie.substring(c_start, c_end))
			}
		}
		return "";
	};

	this.removeCookie = function(name) {
		var cookieStr = name + "=" + escape('null') + ";expires="
				+ new Date().toGMTString();
		document.cookie = cookieStr;
	};

}

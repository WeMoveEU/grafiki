(function ($) {
    // API URL
    var apiUrl = AJS.contextPath() + "/rest/grafiki-admin/1.0/";
 
    $(document).ready(function() {
        // request the config information from the server
        $.ajax({
            url: apiUrl,
            dataType: "json"
        }).done(function(config) { // when the configuration is returned...
            // ...populate the form.
            $("#base_url").val(config.baseUrl);
            $("#secret_key").val(config.secretKey);

						$("#admin").submit(function(e) {
								e.preventDefault();
								updateConfig();
						});
        });
    });

		function updateConfig() {
			$.ajax({
				url: apiUrl,
				type: "PUT",
				contentType: "application/json",
				data: '{ "baseUrl": "' + $("#base_url").attr("value") + '", "secretKey": "' +  $("#secret_key").attr("value") + '" }',
				processData: false
			});
		}

})(AJS.$ || jQuery);

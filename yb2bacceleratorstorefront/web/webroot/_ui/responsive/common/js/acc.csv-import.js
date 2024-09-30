ACC.csvimport = {
    TEXT_CSV_CONTENT_TYPE: 'text/csv',
    TEXT_CSV_LONG_CONTENT_TYPE: 'text/comma-separated-values',
    APP_EXCEL_CONTENT_TYPE: 'application/vnd.ms-excel',
    js_file_upload_input: '.js-file-upload__input',
    js_file_upload_file_name: '.js-file-upload__file-name',
    import_csv_alerts: '#import-csv-alerts',

    _autoload: [
        ["changeFileUploadAppearance", $(".js-file-upload").length != 0],
        ["bindImportCSVActions", $(".js-import-csv").length != 0]
    ],

    changeFileUploadAppearance: function() {
        $(ACC.csvimport.js_file_upload_input).on('change',function () {
            var files = (this.files);
            var spanFileNames = document.createElement("span");
            spanFileNames.insertAdjacentText("beforeend", files[0].name.toLowerCase());
            spanFileNames.insertAdjacentElement("beforeend", document.createElement("br"));
			
            $(ACC.csvimport.js_file_upload_file_name).unbind('mouseenter mouseleave');
            $(ACC.csvimport.js_file_upload_file_name).html(spanFileNames);

            if($('.js-file-upload').parents('#cboxLoadedContent').length > 0){
                ACC.colorbox.resize();
            }
        })
    },

    bindImportCSVActions: function() {
        $('#chooseFileButton').on('click', function (event) {
            ACC.csvimport.clearGlobalAlerts();
        });

        $('#importButton').on('click', function (event) {
            event.preventDefault();
            ACC.csvimport.clearGlobalAlerts();

            if ($(ACC.csvimport.js_file_upload_input).val().trim().length <= 0) {
                ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-no-file-chosen-error-message'});
                return;
            }

            var selectedFile = document.getElementById('csvFile').files[0];
            if (!ACC.csvimport.isSelectedFileValid(selectedFile)) {
                return;
            }

            var form = document.getElementById('importCSVSavedCartForm');
            var formData = new window.FormData(form);
            formData.append("csvFile", selectedFile);

            ACC.csvimport.displayGlobalAlert({type: 'warning', messageId: 'import-csv-upload-message'});
            ACC.csvimport.enableDisableActionButtons(false);

            $.ajax({
                url: form.action,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                success: function() {
                    ACC.csvimport.displayGlobalAlert({type: 'info', message: ''});
                    $('#import-csv-alerts .alert-info').append($('#import-csv-success-message').html());

                    ACC.csvimport.clearChosenFile();
                },
                error: function(jqXHR) {
                    if (jqXHR.status == 400) {
                        if (jqXHR.responseJSON) {
                            ACC.csvimport.displayGlobalAlert({type: 'error', message: jqXHR.responseJSON});
                            return;
                        }
                    }

                    ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-generic-error-message'});
                },
                complete: function() {
                    ACC.csvimport.enableDisableActionButtons(true);
                }
            });
        });
    },

    isSelectedFileValid: function(selectedFile) {
        if (window.File && window.Blob && selectedFile) {
            var CONTENT_TYPE = new Set([ACC.csvimport.TEXT_CSV_CONTENT_TYPE, ACC.csvimport.APP_EXCEL_CONTENT_TYPE,
                ACC.csvimport.TEXT_CSV_LONG_CONTENT_TYPE]);
            if (!CONTENT_TYPE.has(selectedFile.type)) {
                ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-file-csv-required'});
                return false;
            }

            var fileName = selectedFile.name;
            if (!fileName || !(/\.csv$/i).test(fileName)) {
                ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-file-csv-required'});
                return false;
            }

            var fileMaxSize = $('.js-file-upload__input').data('file-max-size');
            if ($.isNumeric(fileMaxSize) && selectedFile.size > parseFloat(fileMaxSize)) {
                ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-file-max-size-exceeded-error-message'});
                return false;
            }
        }

        return true;
    },

    displayGlobalAlert: function(options) {
        ACC.csvimport.clearGlobalAlerts();
        var alertTemplateSelector;

        switch (options.type) {
            case 'error':
                alertTemplateSelector = '#global-alert-danger-template';
                break;
            case 'warning':
                alertTemplateSelector = '#global-alert-warning-template';
                break;
            default:
                alertTemplateSelector = '#global-alert-info-template';
        }

        if (typeof options.message != 'undefined') {
            $(ACC.csvimport.import_csv_alerts).append($(alertTemplateSelector).tmpl({message: options.message}));
        }

        if (typeof options.messageId != 'undefined')
        {
            $(ACC.csvimport.import_csv_alerts).append($(alertTemplateSelector).tmpl({message: $(document).find('#' + options.messageId).text()}));
        }

        $(".closeAccAlert").on("click", function () {
            $(this).parent('.getAccAlert').remove();
        });
    },

    clearGlobalAlerts: function() {
        $(ACC.csvimport.import_csv_alerts).empty();
    },

    clearChosenFile: function() {
        document.getElementById('csvFile').value = '';
        $(ACC.csvimport.js_file_upload_file_name).text('');
    },

    enableDisableActionButtons: function(enable) {
        $('#chooseFileButton').attr('disabled', !enable);
        $('#importButton').prop('disabled', !enable);
    }
}

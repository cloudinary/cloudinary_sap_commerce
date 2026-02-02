ACC.verificationtoken = {
    _autoload: [
            "bindSendVerificationTokenLink",
            "enableVerificationTokenLink"
    ],

    bindSendVerificationTokenLink: function() {
        let linkClicked = false;
        let countdownTimer;

        const sendVerificationTokenLink = $('.send-verification-token-link');
        const resendVerificationTokenLink = $('.resend-verification-token-link');
        const otpUserName = $('#otpUserName');
        const otpPassword = $('#otpPassword');
        const lastOtpUserName = $("#lastOtpUserName");
        const jUsername = $("#j_username");
        const sentOtpTokenInfo = $(".sent_otp_token_info");
        const helpMessage = $("#helpMessage");

        $(document).on("click", ".send-verification-token-link, .resend-verification-token-link", (e) => {
            if (linkClicked) {
                return;
            }

            // create verification code
            $.ajax({
                url: ACC.config.encodedContextPath + "/verificationToken",
                type: "POST",
                data : {
                    username : otpUserName.val(),
                    password : otpPassword.val()
                },
                success: (data) => {
                    lastOtpUserName.val(otpUserName.val());
                    jUsername.val(data);
                    linkClicked = true;
                    let secondsRemaining = 60;
                    sendVerificationTokenLink.css("text-transform", "none");
                    sendVerificationTokenLink.addClass('disabled-link').text(`${secondsRemaining}s`);
                    resendVerificationTokenLink.hide();
                    sendVerificationTokenLink.show();

                    countdownTimer = setInterval(() => {
                    secondsRemaining--;
                    sendVerificationTokenLink.text(`${secondsRemaining}s`);
                    if (secondsRemaining <= 0) {
                        clearInterval(countdownTimer);
                        linkClicked = false;
                        resendVerificationTokenLink.show();
                        sendVerificationTokenLink.hide();
                    }
                }, 1000);

                sentOtpTokenInfo.fadeIn().delay(10000).fadeOut();
                $(".send_otp_token_for_error_info").hide();
                },
                error: (xhr, textStatus, error) => {
                    if(xhr.responseText.includes("Max number of created verification tokens is reached.")) {
                        let errorMessageElement = $(".send_otp_token_for_error_info");
                        let secondsRemainingForError = parseInt($("#secondsForLogin").val(), 10);

                        sendVerificationTokenLink.css("text-transform", "none");
                        sendVerificationTokenLink.addClass('disabled-link').text(`${secondsRemainingForError}s`);
                        resendVerificationTokenLink.hide();
                        sendVerificationTokenLink.show();


                        let countdownTimer = setInterval(() => {
                            secondsRemainingForError--;
                            sendVerificationTokenLink.text(`${secondsRemainingForError}s`);

                            if (secondsRemainingForError <= 0) {
                                clearInterval(countdownTimer);
                                sendVerificationTokenLink.hide();
                                resendVerificationTokenLink.show();
                                errorMessageElement.hide();
                            }
                        }, 1000);
                        errorMessageElement.show();
                    }
                    else {
                        console.error(`Failed to create verification token. Error details [${xhr}, ${textStatus}, ${error}]`);
                    }
                }
            });

        });
    },

    enableVerificationTokenLink: function () {
        $(document).on("input", "#otpUserName, #otpPassword", function(e) {
            var username = $('#otpUserName').val().trim();
            var password = $('#otpPassword').val().trim();

            var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            var isUserNameValid = !username.includes('@') || emailRegex.test(username);

            if (username.includes('@') && !emailRegex.test(username)) {
                $('#otpUserName').addClass('invalid-input');
                $('#helpMessage').removeClass('display-none');
            }
            else {
                $('#otpUserName').removeClass('invalid-input');
                $('#helpMessage').addClass('display-none');
            }

            if (username != '' && password != '' && isUserNameValid) {
                $('.send-verification-token-link').removeClass('disabled-link').removeAttr('href');
                $('.resend-verification-token-link').removeClass('disabled-link').removeAttr('href');
            } else {
                $('.send-verification-token-link').addClass('disabled-link').attr('href', '#');
                $('.resend-verification-token-link').removeClass('disabled-link').removeAttr('href');
            }
        })
    }
};


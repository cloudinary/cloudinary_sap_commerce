ACC.registrationwithverificationtoken = {
    _autoload: [
            "bindSendVerificationTokenForRegistrationLink",
            "enableVerificationTokenForRegistrationLink"
    ],

    bindSendVerificationTokenForRegistrationLink: function() {
        let linkClicked = false;
        let countdownTimer;

        const sendVerificationTokenForRegistrationLink = $('.send-verification-token-for-registration-link');
        const resendVerificationTokenForRegistrationLink = $('.resend-verification-token-for-registration-link');
        var otpUserName;
        if ($('#register\\.confirm\\.email').length > 0) {
            otpUserName = $('#register\\.confirm\\.email');
        } else {
            otpUserName = $('#email');
        }
        const sentOtpTokenInfo = $(".sent_otp_token_for_registration_info");
        const jUsername = $("#verificationTokenId");
        const helpMessage = $("#helpMessage");

        $(document).on("click", ".send-verification-token-for-registration-link, .resend-verification-token-for-registration-link", (e) => {
            if (linkClicked) {
                return;
            }

            // create verification code for registration purpose
            $.ajax({
                url: ACC.config.encodedContextPath + "/verificationToken/register",
                type: "POST",
                data : {
                    username : otpUserName.val()
                },
                success: (data) => {
                    jUsername.val(data);
                    linkClicked = true;
                    let secondsRemaining = 60;
                    sendVerificationTokenForRegistrationLink.css("text-transform", "none");
                    sendVerificationTokenForRegistrationLink.addClass('disabled-link').text(`${secondsRemaining}s`);
                    resendVerificationTokenForRegistrationLink.hide();
                    sendVerificationTokenForRegistrationLink.show();

                    countdownTimer = setInterval(() => {
                         secondsRemaining--;
                         sendVerificationTokenForRegistrationLink.text(`${secondsRemaining}s`);
                         if (secondsRemaining <= 0) {
                             clearInterval(countdownTimer);
                             linkClicked = false;
                             resendVerificationTokenForRegistrationLink.show();
                             sendVerificationTokenForRegistrationLink.hide();
                         }
                    }, 1000);

                    sentOtpTokenInfo.fadeIn().delay(10000).fadeOut();
                    $(".send_otp_token_for_registration_error_info").hide();
                },
                error: (xhr, textStatus, error) => {
                    if(xhr.responseText.includes("Max number of created verification tokens is reached.")) {
                        let errorMessageElement = $(".send_otp_token_for_registration_error_info");
                        let secondsRemainingForError = parseInt($("#secondsForRegistration").val(), 10);
                        sendVerificationTokenForRegistrationLink.css("text-transform", "none");
                        sendVerificationTokenForRegistrationLink.addClass('disabled-link').text(`${secondsRemainingForError}s`);
                        resendVerificationTokenForRegistrationLink.hide();
                        sendVerificationTokenForRegistrationLink.show();


                        let countdownTimer = setInterval(() => {
                            secondsRemainingForError--;
                            sendVerificationTokenForRegistrationLink.text(`${secondsRemainingForError}s`);

                            if (secondsRemainingForError <= 0) {
                                clearInterval(countdownTimer);
                                sendVerificationTokenForRegistrationLink.hide();
                                resendVerificationTokenForRegistrationLink.show();
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

    enableVerificationTokenForRegistrationLink: function () {
        $(document).on("input", "#email, #register\\.email, #register\\.confirm\\.email", function (e) {
            var changedElement = $(e.target);
            var username = changedElement.val().trim();
            var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            var isUserNameValid = emailRegex.test(username);
            var emailConfirmed = true;
            var orginalEmail = $(".js-secureportal-orignal-register-email").val();
            var confirmationEmail = $(".js-secureportal-confirm-register-email").val();
            if (orginalEmail !== confirmationEmail) {
                emailConfirmed = false;
            }

            if (!isUserNameValid) {
                changedElement.addClass('invalid-input');
                $('#helpMessage').removeClass('display-none');
                $('.send-verification-token-for-registration-link').addClass('disabled-link').attr('href', '#');
                $('.resend-verification-token-for-registration-link').addClass('disabled-link').attr('href', '#');
            }
            else {
                changedElement.removeClass('invalid-input');
                $('#helpMessage').addClass('display-none');
                $('.send-verification-token-for-registration-link').removeClass('disabled-link').removeAttr('href');
                $('.resend-verification-token-for-registration-link').removeClass('disabled-link').removeAttr('href');
            }

            if(!emailConfirmed){
                $('.send-verification-token-for-registration-link').addClass('disabled-link').attr('href', '#');
                $('.resend-verification-token-for-registration-link').addClass('disabled-link').attr('href', '#');
            }
        });
      
        if($('#email').length > 0){
            var username = $('#email').val().trim();
            var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            var isUserNameValid = emailRegex.test(username);
            if (isUserNameValid) {
                $('.send-verification-token-for-registration-link').removeClass('disabled-link').removeAttr('href');
                $('.resend-verification-token-for-registration-link').removeClass('disabled-link').removeAttr('href');
            }
        }
    }
};


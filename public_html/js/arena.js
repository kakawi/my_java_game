//$(function(){
    console.log("Arena");

    var opts = {
        lines: 13 // The number of lines to draw
        , length: 28 // The length of each line
        , width: 14 // The line thickness
        , radius: 42 // The radius of the inner circle
        , scale: 1 // Scales overall size of the spinner
        , corners: 1 // Corner roundness (0..1)
        , color: '#000' // #rgb or #rrggbb or array of colors
        , opacity: 0.25 // Opacity of the lines
        , rotate: 0 // The rotation offset
        , direction: 1 // 1: clockwise, -1: counterclockwise
        , speed: 1 // Rounds per second
        , trail: 60 // Afterglow percentage
        , fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
        , zIndex: 2e9 // The z-index (defaults to 2000000000)
        , className: 'spinner' // The CSS class to assign to the spinner
        , top: '50%' // Top position relative to parent
        , left: '50%' // Left position relative to parent
        , shadow: false // Whether to render a shadow
        , hwaccel: false // Whether to use hardware acceleration
        , position: 'relative' // Element positioning
    };


    var spinnerPanel = new Spinner(opts);
    var spinnerGameList = new Spinner(opts);

    function startSpin(type) {
        var target;
        if (type === "list_of_games") {
            target = $('.games');
            target.empty();
            spinnerGameList.spin(target[0]);
        } else if (type === "panel") {
            target = $('.panel');
            target.empty();
            spinnerPanel.spin(target[0]);
        }
    }

    function stopSpin(type) {
        if (type === "list_of_games") {
            spinnerGameList.stop();
        } else if (type === "panel") {
            spinnerPanel.stop();
        }
    }

    var Player = function() {
        var self = this;

        this.listeners = {};

        self.addListener = function(object, event, callback) {
            if(!self.listeners.hasOwnProperty(event)) {
                self.listeners[event] = [];
            }

            self.listeners[event].push(function(args) {
                callback.call(object, args);
            });
        };

        self.triggerEvent = function(event, args) {
            if(self.listeners.hasOwnProperty(event)) {
                for(var i = 0; i < self.listeners[event].length; ++i) {

                    self.listeners[event][i](args);
                }
            }
        };

        self.setStatus = function(status) {
            self.status = status;
            self.triggerEvent("change_status", status);
        };

        self.getStatus = function() {
            return self.status;
        };

        self.setGameSessionId = function(gameSessionId) {
            self.gameSessionId = gameSessionId;
        };

        self.getGameSessionId = function() {
            return self.gameSessionId;
        };


    };

    var player = new Player();

    var Arena = function(player) {

        var self = this;
        var ws;
        ws = new WebSocket("ws://" + window.location.hostname + ":8080/game");

        ws.onopen = function (event) {
            self.init();
        };

        function sendMessage(message) {
            console.log("Send: " + message);
            ws.send(message);
        }

        ws.onmessage = function (event) {
            var json = JSON.parse(event.data);
            switch (json['action']) {
                case 'get_status':
                    responseGetStatus(json);
                    break;
                case 'apply_offer':
                    responseApplyOffer(json);
                    break;
                case 'get_games':
                    responseGetGames(json);
                    break;
                case 'start_game':
                    responseStartGame(json);
                    break;
                case 'cancel_offer':
                    responseCancelOffer(json);
                    break;
                case 'increase_click_count':
                    responseIncreaseClickCount(json);
                    break;
                case 'finish_game':
                    responseFinishGame(json);
                    break;
            }

            console.log("Return: " + event.data)
        };

        ws.onclose = function (event) {
            if (event.wasClean) {
                console.log('Соединение закрыто чисто');
            } else {
                console.log('Обрыв соединения'); // например, "убит" процесс сервера
            }
            console.log('Код: ' + event.code + ' причина: ' + event.reason);
        };

        function responseApplyOffer(data) {
            switch (data['status']) {
                //case 'wait':
                //    startSpin();
                //    break;
                case 'created':
                    player.setStatus("apply");
                    stopSpin("panel");
                    self.requestGetGames();
                    break;
            }
        };

        function responseGetStatus(data) {
            switch (data['status']) {
                case 'done':
                    player.setStatus(data['player_status']);
                    stopSpin("panel");
                    var source;
                    var template;
                    var html;
                    var panel;
                    if (player.getStatus() === "default") {
                        source = $("#panel-apply-template").html();
                        template = Handlebars.compile(source);
                        html    = template();
                        panel = $(".panel");
                        panel.html(html);
                    } else if (player.getStatus() === "apply"){
                        player.setGameSessionId(data['gameSessionId']);
                        source = $("#panel-cancel-template").html();
                        template = Handlebars.compile(source);
                        html    = template(data);
                        panel = $(".panel");
                        panel.html(html);
                    }
                    break;
            }
        };

        function responseGetGames(data) {
            switch (data['status']) {
                //case 'wait':
                //    startSpin();
                //    break;
                case 'done':
                    stopSpin("list_of_games");
                    var games = data.games;
                    if (player.getStatus() === 'default') {
                        self.showOffersWithButton(games);
                    } else {
                        self.showOffersWithoutButton(games);
                    }

                    break;
            }
        };

        function responseCancelOffer(data) {
            switch (data['status']) {
                //case 'wait':
                //    startSpin();
                //    break;
                case 'done':
                    player.setStatus("default");
                    stopSpin("panel");
                    var source = $("#panel-apply-template").html();
                    var template = Handlebars.compile(source);
                    var html    = template();
                    var panel = $(".panel");
                    panel.html(html);

                    self.requestGetGames();
                    break;
            }
        };

        function responseIncreaseClickCount(data) {
            switch (data['status']) {
                case 'done':
                    var source = $("#source-template").html();
                    var template = Handlebars.compile(source);
                    var html    = template(data);
                    var panel = $(".source");
                    panel.html(html);
                    break;
            }
        };

        function responseFinishGame(data) {
            switch (data['status']) {
                case 'done':
                    self.hideGame();
                    self.showResult(data);
                    self.showApply();
                    self.enablePanel();
                    self.enabledOffers();
                    break;
            }
        };

        function responseStartGame(data){
            switch (data['status']) {
                //case 'wait':
                //    startSpin();
                //    break;
                case 'done':
                    player.setGameSessionId(data['gameSessionId']);
                    self.showGame();
                    self.hideResult();
                    self.disabledPanel();
                    self.disabledOffers();
                    break;
            }
        };

        self.requestGetStatus = function() {
            startSpin("panel");
            var data = {
                action: "get_status"
            };
            sendMessage(JSON.stringify(data))
        };

        self.requestApplyOffer = function() {
            startSpin("panel");
            var data = {
                action: "apply_offer"
            };
            sendMessage(JSON.stringify(data))
        };

        self.requestGetGames = function() {
            startSpin("list_of_games");
            var data = {
                action: "get_games",
            };
            sendMessage(JSON.stringify(data));
        };

        self.requestAcceptOffer = function(gameSessionId) {
            var data = {
                action: "accept_offer",
                data: {
                    "gameSessionId": gameSessionId
                }
            };
            sendMessage(JSON.stringify(data));
        };

        self.requestIncreaseClickCount = function() {
            var data = {
                action: "increase_click_count",
                data: {
                    "gameSessionId": player.getGameSessionId()
                }
            };
            sendMessage(JSON.stringify(data));
        };

        self.requestCancelOffer = function() {
            startSpin("panel");
            var data = {
                action: "cancel_offer"
            };
            sendMessage(JSON.stringify(data));
        };

        self.setFunction = function() {
            player.addListener(this, "change_status", self.changeStatus);

            $('body').on("click", '.apply', function() {
                self.requestApplyOffer();
            });
            $('body').on("click", '.accept', function(e) {
                var button = $(e.currentTarget);
                var gameSessionId = button.data('gamesessionid');
                self.requestAcceptOffer(gameSessionId);
            });
            $('body').on('click', '.cancel', function() {
                self.requestCancelOffer();
            });
            $('.game').on("click", '.game_object', function() {
                self.requestIncreaseClickCount();
            });


        };

        self.changeStatus = function(status) {
            switch (status) {
                case "default":
                    self.showApply();
                    break;
                case "apply":
                    self.showCancel();
                    break;
                case "inGame":
                    self.showGame();
                    break;
            }
        };

        self.showApply = function() {
            var source = $("#panel-apply-template").html();
            var template = Handlebars.compile(source);
            var html    = template();
            var panel = $(".panel");
            panel.html(html);
        };

        self.showCancel = function() {
            var source = $("#panel-cancel-template").html();
            var template = Handlebars.compile(source);
            var html    = template();
            var panel = $(".panel");
            panel.html(html);
        };

        self.showOffersWithButton = function(games) {
            var source = $("#game-list-with-button-template").html();
            var template = Handlebars.compile(source);
            var html    = template({games: games});
            $('.games').html(html);
        };

        self.showOffersWithoutButton = function(games) {
            var source = $("#game-list-without-button-template").html();
            var template = Handlebars.compile(source);
            var html    = template({games: games});
            $('.games').html(html);
        };

        self.showGame = function() {
            $('.game').show();
        };
        self.hideGame = function() {
            $('.game').hide();
        };

        self.disabledPanel = function() {
            $('.panel button').prop('disabled', true);
        };

        self.disabledOffers = function() {
            $('.games button').prop('disabled', true);
        };

        self.enablePanel = function() {
            $('.panel button').prop('disabled', false);
        };

        self.enabledOffers = function() {
            $('.games button').prop('disabled', false);
        };

        self.showResult = function(data) {
            var result;
            var scorePlayer = data['score_player'];
            var scoreEnemy = data['score_enemy'];
            if (scorePlayer > scoreEnemy) {
                result = "Вы выиграли";
            } else if (scorePlayer < scoreEnemy) {
                result = "Вы проиграли";
            } else {
                result = "Ничья";
            }
            data['result'] = result;
            var source = $("#finish-game-template").html();
            var template = Handlebars.compile(source);
            var html    = template(data);
            var target = $(".result");
            target.html(html).show();
        };

        self.hideResult = function() {
            $(".result").hide();
        };



        self.init = function() {
            self.setFunction();
            self.requestGetStatus();
            self.requestGetGames();
        };


        return self;
    };

    var arena = new Arena(player);

//});


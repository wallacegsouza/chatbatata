(function() {
    "use strict";

    var connection = null;
    var nome = null;
    var $view = document.getElementById('viewmsg');
    var $texto = document.getElementById('texto');
    var $nome = document.getElementById('nome');
    var $uploadbtn =  document.getElementById('upload');
    var n_buff = 0;
    var buffer = [];
    var icone = getIconImage();
    var anim = 'animated';

    var color = { partial : 'hsl(' + Math.floor(Math.random() * 360) + ',' + Math.floor(Math.random() * 100) + '%,' };
    color.background = color.partial + '95%)';
    color.border = color.partial + '75%)';
    color.color = color.partial + '30%)';

    function connect(_nome) {

        var nome = _nome;
        connection =  new WebSocket("ws://" + location.hostname + ":" + location.port + "/echo");

        connection.onopen = function () {
            var data = new Date().toJSON().slice(11, 19);
            connection.send('<div class="browser"><u>➥</u><b>' + nome + '</b> entrou em ' + data + ' com o browser ' + navigator.userAgent + '</div>');
        };

        // Log errors
        connection.onerror = function (error) {
            console.error('WebSocket Error ' + error);
        };

        // Log messages from the server
        connection.onmessage = function (e) {
            var str = e.data;
            if (str) {
                var s = str.substring(str.lastIndexOf("</i>")+4, str.lastIndexOf("</div>"));
                if(call(s)) return;
                $view.innerHTML += e.data;
                updateScroll($view);
            }
        };
    }

    function getMSG(text) {
        var data = new Date().toJSON().slice(11, 19);
        return '<div class="msg ' + anim + '" style="color:' + color.color + ';border-color:' + color.border + ';background-color:' + color.background + ';">' +
            '<u class="icone" style="' + icone + '"></u>' +
            '<i>' + $nome.value + '@'  + data + '</i> ' +
            (text || $texto.value) + '</div>';
    }

    function send() {
        connection.send( getMSG() );
        buffer.push($texto.value);
        $texto.value = "";
        $texto.focus();
    }

    function signin(nome) {
        nome = $nome.value;
        if(validarNome(nome))  {
            document.body.classList.add('chat');
            $texto.focus();
            connect(nome);
        }
    }

    function validarNome(nome) {
        return nome.trim().length > 0;
    }

    function call(func) {
        try {
            return eval(func);
        } catch(e) {}
        return false;
    }

    function clean() {
        $view.innerHTML = "";
        $texto.value = "";
        return true;
    }

    function upload() {
        var uploadview = window.open("/upload", "_blank", "menubar=no,status=no,titlebar=no,toolbar=no,scrollbars=no,resizable=no,top=80,left=80,width=400,height=80");
        uploadview.onbeforeunload = function () {
            setTimeout(function () {
                var comp = uploadview.document.getElementById('comp');
                var html = comp.innerHTML || "";
                connection.send(getMSG(html.trim()));
                //buffer.push(comp.innerHTML);
                uploadview.close();
            },500);
        };
    }

    function updateScroll(div) {
        div.scrollTop = div.scrollHeight - div.clientHeight;
    }

    $texto.addEventListener('keyup', function(e) {
        var key = e.keyCode;
        if(key === 13) {//enter
            send();
        } else if (key === 38) {//seta para cima
            var n = buffer.length - ++n_buff;
            if (n >= 0) $texto.value = buffer[n];
        } else if(key === 27) {//esc
            $texto.value = '';
            n_buff = 0;
        }
    });

    $nome.addEventListener('keyup', function(e) {
        var key = e.keyCode;
        if(key === 13) {//enter
            signin();
        } else if(key === 27) {//esc
            this.value = '';
            this.blur();
        }
    });

    $uploadbtn.addEventListener('click', upload);

    function getIconImage() {
        var icons = [
            'OOKEIp',
            'L2yNVb',
            'APCnU8',
            '8sL9Y8',
            'oFUCGe',
            'MDApDb',
            'BhJpoz',
            'mJo5BW',
            'U5vYEy',
            'fWfHAB'
        ];

        return 'background-image: url(http://goo.gl/' + icons[Math.floor(Math.random() * icons.length)] + ');';
    }

    //easter potato
    function batata() { document.body.classList.toggle('batata'); }

    document.body.onload = function() {
        $nome.focus();
    }


    //animação para cada nova mensagem
    var msg = {};
    var observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            msg = document.querySelector('.' + anim);
            if(msg) msg.classList.remove(anim);
        });
    });

    observer.observe($view, { attributes: true, childList: true, characterData: true });

})();
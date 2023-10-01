// API configuration wit RoundRobi algorithm.
URL_API = `http://${window.location.hostname}:8080`;
// get html elements
let boton_send = document.getElementById("btn-send");
let boton_viewdata = document.getElementById("btn-view-data");
let input_message = document.getElementById("input_message");
let messages_list_html = document.getElementById("msg-list");
let msg = null;
let msg_list_data = [];

//send message to backend
boton_send.onclick = async() => {
    msg = input_message.value;
    //clean de list input_message
    input_message.value = "";
    const request = {
        method: 'POST',
        mode: 'cors',
        Headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            data: msg
        })
    };
    const response = await fetch(`${URL_API}/api/messages`, request);
    const receivedData = await response.text();
    console.log(receivedData);
};

//get message from backend
boton_viewdata.onclick = async() => {
    //clean de list Html
    messages_list_html.innerHTML = "";
    // fetching data
    const response = await fetch(`${URL_API}/api/messages`);
    let receivedData = await response.text();
    receivedData = JSON.parse(receivedData);
    // add messages to Html list
    Object.keys(receivedData).map( key => {
        list_item = document.createElement('li');
        list_item.innerHTML = receivedData[key];
        messages_list_html.appendChild(list_item);
    });

};

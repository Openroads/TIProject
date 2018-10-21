from channels.generic.websocket import WebsocketConsumer
import json


class ChatConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()

    def disconnect(self, close_code):
        pass

    def receive(self, text_data):
        print(type(text_data))
        text_data_json = json.loads(text_data)
        print(type(text_data_json))
        message = text_data_json['message']

        self.send(text_data=json.dumps({
            'message': message
        }))


class HelloConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()

    def disconnect(self, close_code):
        pass

    def receive(self, text_data):
        print(type(text_data))
        #text_data_json = json.loads(text_data)
        #print(type(text_data_json))

        #name = text_data_json['name']

        self.send(text_data=json.dumps({
            'message': "Hello from web socket " + text_data + " !"
        }))
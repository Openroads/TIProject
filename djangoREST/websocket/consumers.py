from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
import json


class ChatConsumer(WebsocketConsumer):
    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'chat_%s' % self.room_name
        print("Connecting to room name: " + self.room_name)
        # Join room group
        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name,
            self.channel_name
        )

        self.accept()

    def disconnect(self, close_code):
        # Leave room group
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    # Receive message from WebSocket
    def receive(self, text_data):
        print(self.scope['url_route']['kwargs']['room_name'])
        text_data_json = json.loads(text_data)
        message = text_data_json['message']
        print("Received message data from websocket: " + message)
        # Send message to room group
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_name,
            {
                'type': 'chat_message',
                'message': message
            }
        )

    # Receive message from room group
    def chat_message(self, event):
        message = event['message']

        print("Receive and send message from group room: " + message)
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'message': message
        }))


class DocumentChatConsumer(WebsocketConsumer):
    def connect(self):
        self.room_id = self.scope['url_route']['kwargs']['room_id']
        self.room_group_id = 'chat_%s' % self.room_id
        print("[Document WS] Connecting to room name: " + self.room_group_id)
        # Join room group
        async_to_sync(self.channel_layer.group_add)(
            self.room_group_id,
            self.channel_name
        )

        self.accept()

    def disconnect(self, close_code):
        # Leave room group
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_id,
            self.channel_name
        )

    # Receive message from WebSocket
    def receive(self, text_data):
        text_data_json = json.loads(text_data)

        print("[Document WS] Received message data from web-socket: " + text_data)
        # Send message to room group
        async_to_sync(self.channel_layer.group_send)(
            self.room_group_id,
            {
                'type': 'chat_message',
                'message': text_data_json
            }
        )

    # Receive message from room group
    def chat_message(self, event):
        message = event['message']
        print("[Document WS] Receive and send message from group room: ")
        print(message)
        # Send message to WebSocket
        self.send(text_data=json.dumps(message))


GROUP_NAME_ALL = "FILE_OPERATIONS_BROADCAST"


class BroadcastConsumer(WebsocketConsumer):
    def connect(self):

        # Join room group
        async_to_sync(self.channel_layer.group_add)(
            GROUP_NAME_ALL,
            self.channel_name
        )

        self.accept()

    def disconnect(self, close_code):
        # Leave room group
        async_to_sync(self.channel_layer.group_discard)(
            GROUP_NAME_ALL,
            self.channel_name
        )

    # Receive message from WebSocket
    def receive(self, text_json_data):
        received_json = json.loads(text_json_data)

        file_operation = received_json['file_operation']

        print("File operation: " + file_operation)

        # make operation in database for each file operation
        # if file_operation == 'lock-file':
        #   Document.objects.filter(pk=message).update(editingBy=received_json['editingBy'])
        # elif file_operation == 'unlock-file':
        #   Document.objects.filter(pk=received_json['documentId']).update(editingBy=None)
        # elif file_operation == 'add-file':
        #   Document.objects.create(received_json).save()
        # elif file_operation == 'remove-file':
        #
        # else:
        #     print("Unsupported file operation: " + file_operation)


        # Send message to room group
        async_to_sync(self.channel_layer.group_send)(
            GROUP_NAME_ALL,
            {
                'type': 'broadcast_file_operation_message',
                'message': text_json_data
            }
        )

    # Receive message from room group
    def chat_message(self, event):
        message = event['message']
        print("Received message from group " + message)
        # Send message to WebSocket
        self.send(message)


class HelloConsumer(WebsocketConsumer):
    def connect(self):
        self.accept()

    def disconnect(self, close_code):
        pass

    def receive(self, text_data):
        print(type(text_data))

        self.send(text_data=json.dumps({
            'message': "Hello from web socket " + text_data + " !"
        }))
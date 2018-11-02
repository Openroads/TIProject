from django.conf.urls import url

from . import consumers


websocket_urlpatterns = [
    url(r'^ws/chat/(?P<room_name>[^/]+)/$', consumers.ChatConsumer),
    url(r'^ws/chat/(?P<room_id>[0-9]+)$', consumers.DocumentChatConsumer),
    url(r'^ws/broadcast/$', consumers.BroadcastConsumer),
    url(r'^ws/say-hello/$', consumers.HelloConsumer),
]
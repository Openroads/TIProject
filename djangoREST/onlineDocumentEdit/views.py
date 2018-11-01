import json

from django.contrib.auth import authenticate
from django.contrib.auth.models import User
from django.http import HttpResponse
from django.views import View
from rest_framework import generics
from rest_framework.renderers import JSONRenderer
from rest_framework.views import APIView

from onlineDocumentEdit.serializers import DocumentListItemSerializer, DocumentDetailsSerializer, UserSerializer
from .models import Document


# Create your views here.
class ListDocumentsView(generics.ListCreateAPIView):
    """
    Provides a get method handler.
    """
    queryset = Document.objects.all()
    serializer_class = DocumentListItemSerializer


class DocumentsDetailView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Document.objects.all()
    serializer_class = DocumentDetailsSerializer


class DocumentLockView(APIView):
    def post(self, request, documentId, userId):
        Document.objects.filter(pk=documentId).update(editingBy=userId)
        return HttpResponse('')


class DocumentUnlockView(APIView):
    def post(self, request, documentId):
        Document.objects.filter(pk=documentId).update(editingBy=None)
        return HttpResponse('')


class UserList(generics.ListAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserDetail(generics.RetrieveAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserLogin(View):

    def post(self, request):
        json_user = json.loads(request.body)
        print(json_user)
        user = authenticate(username=json_user['username'],password=json_user['password'])
        serializer = UserSerializer(user).data
        user_json = JSONRenderer().render(serializer)
        return HttpResponse(user_json)

from django.contrib.auth.models import User
from rest_framework import generics

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


class UserList(generics.ListAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer


class UserDetail(generics.RetrieveAPIView):
    queryset = User.objects.all()
    serializer_class = UserSerializer

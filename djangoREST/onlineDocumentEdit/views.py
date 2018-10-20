from django.shortcuts import render

from rest_framework import generics

from onlineDocumentEdit.serializers import DocumentListItemSerializer, DocumentDetailsSerializer
from .models import Document


# Create your views here.
class ListDocumentsView(generics.ListAPIView):
    """
    Provides a get method handler.
    """
    queryset = Document.objects.all()
    serializer_class = DocumentListItemSerializer


class DocumentsDetailView(generics.RetrieveUpdateDestroyAPIView):
    queryset = Document.objects.all()
    serializer_class = DocumentDetailsSerializer

from django.shortcuts import render

from rest_framework import generics
from .models import Document
from .serializers import DocumentSerializer


# Create your views here.
class ListDocumentsView(generics.ListAPIView):
    """
    Provides a get method handler.
    """
    queryset = Document.objects.all()
    serializer_class = DocumentSerializer

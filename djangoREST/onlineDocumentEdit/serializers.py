from rest_framework import serializers
from .models import Document


class DocumentListItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = Document
        fields = ("id", "title", "version")


class DocumentDetailsSerializer(serializers.ModelSerializer):
    class Meta:
        model = Document
        fields = '__all__'

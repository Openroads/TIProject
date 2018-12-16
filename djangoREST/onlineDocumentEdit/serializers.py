from django.contrib.auth.models import User
from rest_framework import serializers
from .models import Document


class DocumentListItemSerializer(serializers.ModelSerializer):
    editingBy = serializers.ReadOnlyField(source='editingBy.username')

    class Meta:
        model = Document
        fields = ("id", "title", "content", "editingBy")


class DocumentDetailsSerializer(serializers.ModelSerializer):
    editingBy = serializers.ReadOnlyField(source='editingBy.username')

    class Meta:
        model = Document
        fields = '__all__'


class UserSerializer(serializers.ModelSerializer):

    class Meta:
        model = User
        fields = ('id', 'username')
import json
import boto3
from googletrans import Translator

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('messages')

def lambda_handler(event, context):
    body = json.loads(event["application/json"])
    message_id = body['message_id']
    response = table.get_item(Key={'message_id': message_id})

    if 'Item' not in response:
        return {
            'statusCode': 404,
            'body': json.dumps({'error': 'Message not found - ' + message_id })
        }

    message = response['Item']
    desired_language = body['language']
    translated_message = translate_message(message, desired_language)
    translated_message['content'] = message['content'] + "\n" + translated_message['content']
    return {
        'statusCode': 200,
        'body': json.dumps(translated_message),
        'response': message
    }

def translate_message(message, desired_language):
    text = message['content']
    translator = Translator()
    translation = translator.translate(text, dest=desired_language)

    translated_message = {
        'message_id': message['message_id'],
        'sender_id': message['sender_id'],
        'conversation_id': message['conversation_id'],
        'content': translation.text,
        'message_type': message['message_type'],
        'status': str(message['status']),
        'timeSend': str(message['timeSend']),
        'pathS3': str(message['pathS3'])
    }

    return translated_message
